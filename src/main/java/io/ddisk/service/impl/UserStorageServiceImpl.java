package io.ddisk.service.impl;

import io.ddisk.dao.FileRepository;
import io.ddisk.dao.UserFileRepository;
import io.ddisk.dao.UserStorageRepository;
import io.ddisk.domain.consts.FileConst;
import io.ddisk.domain.entity.FileEntity;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.entity.UserStorageEntity;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.domain.vo.UserStorageVO;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.service.UserStorageService;
import io.ddisk.utils.SpringWebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/24
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserStorageServiceImpl implements UserStorageService {

	@Autowired
	private UserStorageRepository userStorageRepository;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private UserFileRepository userFileRepository;


	/**
	 * 用户必须已登录
	 * 获取用户的存储空间使用信息
	 */
	@Override
	public UserStorageVO getStorageInfo() {
		return getStorageInfo(SpringWebUtils.requireLogin());
	}

	/**
	 * 获取用户的存储空间使用信息
	 *
	 * @param user
	 * @return
	 */
	@Override
	public UserStorageVO getStorageInfo(LoginUser user) {

		assert Objects.nonNull(user);
		UserStorageEntity userStorageEntity = userStorageRepository.findByUserId(user.getId()).orElseGet(() ->
			userStorageRepository.save(new UserStorageEntity(user.getId(), computeUsedStorage(user.getId())))
		);

		return new UserStorageVO(user.getRole().getMaxStorageSize(), userStorageEntity.getUsedStorage());
	}

	/**
	 * 用户必须已登录
	 * 计算用户已使用的空间大小
	 *
	 * @return
	 */
	@Override
	public Long computeUsedStorage() {
		return computeUsedStorage(SpringWebUtils.requireLogin().getId());
	}

	/**
	 * 计算用户已使用的空间大小
	 * @param userId
	 * @return
	 */
	@Override
	public Long computeUsedStorage(Long userId) {

		long size = FileConst.ZERO;
		List<UserFileEntity> userFiles = userFileRepository.findAllByUserIdAndDirAndDelete(userId, false, false);
		if (CollectionUtils.isEmpty(userFiles)){
			return size;
		}
		Set<String> fileIds = userFiles.stream().map(UserFileEntity::getFileId).collect(Collectors.toSet());
		Map<String, Long> fileSize = fileRepository.findAllById(fileIds).stream().collect(Collectors.toMap(FileEntity::getId, FileEntity::getSize));

		size += userFiles.stream().mapToLong(uf -> fileSize.get(uf.getFileId())).sum();
		return size;
	}

	/**
	 * 用户必须已登录
	 * 增加用户已使用空间
	 *
	 * @param size 增加大小
	 */
	@Override
	public UserStorageVO increaseStorage(Long size) {
		return increaseStorage(SpringWebUtils.requireLogin(), size);
	}

	/**
	 * 增加用户已使用空间
	 *
	 * @param user
	 * @param size
	 */
	@Override
	public UserStorageVO increaseStorage(LoginUser user, Long size) {
		Long maxStorageSize = user.getRole().getMaxStorageSize();
		UserStorageEntity userStorageEntity = userStorageRepository.findByUserId(user.getId()).orElseGet(() ->
				userStorageRepository.save(new UserStorageEntity(user.getId(), computeUsedStorage(user.getId())))
		);

		Long usedSize = userStorageEntity.getUsedStorage() + size;
		// 超出用户最大存储容量
		if (usedSize.compareTo(maxStorageSize)>0){
			throw new BizException(BizMessage.USED_STORAGE_OUT_OF_MAX);
		}
		userStorageEntity.setUsedStorage(usedSize);
		userStorageRepository.save(userStorageEntity);
		return new UserStorageVO(maxStorageSize, usedSize);
	}

	/**
	 * 用户必须已登录
	 * 减少用户已使用空间
	 *
	 * @param size
	 * @return
	 */
	@Override
	public UserStorageVO decreaseStorage(Long size) {
		return decreaseStorage(SpringWebUtils.requireLogin(), size);
	}

	/**
	 * 减少用户已使用空间
	 *
	 * @param user
	 * @param size
	 * @return
	 */
	@Override
	public UserStorageVO decreaseStorage(LoginUser user, Long size) {

		UserStorageEntity userStorageEntity = userStorageRepository.findByUserId(user.getId()).orElseGet(() ->
				userStorageRepository.save(new UserStorageEntity(user.getId(), computeUsedStorage(user.getId())))
		);

		long usedSize = userStorageEntity.getUsedStorage() - size;
		if (usedSize<0L){
			usedSize = computeUsedStorage(user.getId());
		}
		userStorageEntity.setUsedStorage(usedSize);
		userStorageRepository.save(userStorageEntity);
		return new UserStorageVO(user.getRole().getMaxStorageSize(), usedSize);
	}

	/**
	 * 用户必须已登录
	 * 删除文件计算内存
	 *
	 * @param userFileEntityList
	 * @param isDelete true删除文件，false恢复删除文件
	 */
	@Override
	public void deleteFileCalculator(List<UserFileEntity> userFileEntityList, boolean isDelete) {

		AtomicLong size = new AtomicLong();
		Set<String> fileUuidSet = userFileEntityList.parallelStream().map(UserFileEntity::getFileId).filter(Objects::nonNull).collect(Collectors.toSet());
		Map<String, Long> fileSizeMap = CollectionUtils.isEmpty(fileUuidSet) ? Collections.emptyMap() : fileRepository.findAllById(fileUuidSet)
				.stream().parallel().collect(Collectors.toMap(FileEntity::getId, FileEntity::getSize));

		userFileEntityList.forEach(userFileEntity -> {
			size.addAndGet(Optional.ofNullable(fileSizeMap.get(userFileEntity.getFileId())).orElse(0L));
		});
		if (isDelete) {
			increaseStorage(size.get());
		} else {
			decreaseStorage(size.get());
		}
	}
}
