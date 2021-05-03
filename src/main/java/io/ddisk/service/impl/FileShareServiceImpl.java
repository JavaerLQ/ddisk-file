package io.ddisk.service.impl;

import io.ddisk.dao.FileRepository;
import io.ddisk.dao.FileShareGroupRepository;
import io.ddisk.dao.FileShareRepository;
import io.ddisk.dao.UserFileRepository;
import io.ddisk.domain.dto.FileShareDTO;
import io.ddisk.domain.entity.FileEntity;
import io.ddisk.domain.entity.FileShareEntity;
import io.ddisk.domain.entity.FileShareGroupEntity;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.service.FileService;
import io.ddisk.service.FileShareService;
import io.ddisk.service.UserFileService;
import io.ddisk.service.UserStorageService;
import io.ddisk.utils.SDateUtils;
import io.ddisk.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class FileShareServiceImpl implements FileShareService {

	@Autowired
	private FileShareRepository fileShareRepository;

	@Autowired
	private FileShareGroupRepository fileShareGroupRepository;

	@Autowired
	private UserFileRepository userFileRepository;

	@Autowired
	private UserFileService userFileService;

	@Autowired
	private FileRepository fileRepository;

	@Autowired
	private UserStorageService userStorageService;

	@Autowired
	private FileService fileService;


	/**
	 * 分享文件
	 *
	 * @param fileShareDTO
	 * @param userId
	 */
	@Override
	public void shareFile(FileShareDTO fileShareDTO, Long userId) {

		FileShareGroupEntity fileShareGroupEntity = fileShareDTO.createFileShareGroupEntity();
		fileShareGroupEntity.setUserId(userId);

		// 获取分享目录树
		List<UserFileEntity> userFileList = userFileService.getChildrenList(fileShareDTO.getUserFileIds(), false);
		// 权限检验, 是否分享他人文件
		userFileList.stream().filter(uf -> !userId.equals(uf.getUserId())).findFirst().ifPresent((userFileEntity) -> {
			throw new BizException(BizMessage.FILE_SHARE_OWNER_ERROR);
		});

		// UserFileEntity转FileShareEntity
		List<FileShareEntity> shareEntityList = userFileList.stream().map(userFileEntity -> {
			FileShareEntity fileShareEntity = new FileShareEntity();
			BeanUtils.copyProperties(userFileEntity, fileShareEntity);

			fileShareEntity.setUserFileId(userFileEntity.getId());
			fileShareEntity.setFileShareGroupId(fileShareGroupEntity.getId());
			fileShareEntity.setDCount(0L);
			fileShareEntity.setSCount(0L);
			fileShareEntity.setShareId(userFileEntity.getId());
			return fileShareEntity;
		}).collect(Collectors.toList());

		shareEntityList.forEach(shareEntity->{
			String id = UUIDUtil.random32();
			shareEntityList.stream().filter(uf->shareEntity.getDir() && shareEntity.getShareId().equals(uf.getPid())).forEach(se->se.setPid(id));
			// 分享文件根文件pid置为null
			if (fileShareDTO.getUserFileIds().contains(shareEntity.getShareId())){
				shareEntity.setPid(null);
			}
			shareEntity.setShareId(id);
		});

		fileShareGroupRepository.save(fileShareGroupEntity);
		fileShareRepository.saveAll(shareEntityList);
	}

	/**
	 * 取消分享文件
	 *
	 * @param shareId
	 * @param userId
	 */
	@Override
	public void cancelShareFile(String shareId, Long userId) {
		fileShareRepository.findById(shareId).ifPresent(fileShareEntity -> {
			FileShareGroupEntity fileShareGroupEntity = fileShareGroupRepository.findById(fileShareEntity.getFileShareGroupId()).orElseThrow(() -> new BizException(BizMessage.FILE_SHARE_GROUP_NOT_EXIST));

			if (!isValidGroup(fileShareGroupEntity)){
				log.warn("用户[{}]操作的分享文件组[{}]已失效", userId, fileShareGroupEntity.getId());
				throw new BizException(BizMessage.FILE_SHARE_INVALID);
			}
			if (!userId.equals(fileShareGroupEntity.getUserId())){
				log.warn("用户[{}]正在操作用户[{}]的分享文件[{}]", userId, fileShareGroupEntity.getUserId(), shareId);
				throw new BizException(BizMessage.FILE_SHARE_OWNER_ERROR);
			}
			fileShareRepository.delete(fileShareEntity);
		});
	}

	/**
	 * 批量取消分享
	 *
	 * @param shareIds
	 * @param userId
	 */
	@Override
	public void batchCancelShareFile(List<String> shareIds, Long userId) {
		List<FileShareEntity> fileShareEntityList = fileShareRepository.findAllById(shareIds);

		Set<String> shareGroupIdSet = fileShareEntityList.stream().map(FileShareEntity::getFileShareGroupId).collect(Collectors.toSet());
		List<FileShareGroupEntity> shareGroupEntityList = fileShareGroupRepository.findAllById(shareGroupIdSet);

		shareGroupEntityList.stream().filter(fileShareGroupEntity -> !userId.equals(fileShareGroupEntity.getUserId())).findAny().ifPresent(fileShareGroupEntity -> {
			log.warn("用户[{}]正在操作用户[{}]的分享文件", userId, fileShareGroupEntity.getUserId());
			throw new BizException(BizMessage.FILE_SHARE_OWNER_ERROR);
		});

		// 查找失效的分享
		shareGroupEntityList.stream().filter(fileShareGroupEntity -> !isValidGroup(fileShareGroupEntity)).findFirst().ifPresent(fileShareGroupEntity -> {
			log.warn("用户[{}]操作的分享文件组[{}]已失效", userId, fileShareGroupEntity.getId());
			throw new BizException(BizMessage.FILE_SHARE_INVALID);
		});

		fileShareRepository.deleteAll(fileShareEntityList);
	}

	/**
	 * 取消文件分享组
	 *
	 * @param shareGroupId
	 * @param userId
	 */
	@Override
	public void cancelShareGroup(String shareGroupId, Long userId) {
		fileShareGroupRepository.findById(shareGroupId).ifPresent(fileShareGroupEntity -> {
			if (!userId.equals(fileShareGroupEntity.getUserId())){
				log.warn("用户[{}]正在操作用户[{}]的分享文件", userId, fileShareGroupEntity.getUserId());
				throw new BizException(BizMessage.FILE_SHARE_OWNER_ERROR);
			}
			if (isValidGroup(fileShareGroupEntity)){
				fileShareGroupEntity.setActive(false);
				fileShareGroupRepository.save(fileShareGroupEntity);
			}
		});
	}

	/**
	 * 批量取消文件分享组
	 *
	 */
	@Override
	public void batchCancelShareGroup(List<String> shareGroupIds, Long userId) {
		List<FileShareGroupEntity> shareGroupEntityList = fileShareGroupRepository.findAllById(shareGroupIds);
		shareGroupEntityList.forEach(fileShareGroupEntity -> {
			if (!userId.equals(fileShareGroupEntity.getUserId())){
				log.warn("用户[{}]正在操作用户[{}]的分享文件", userId, fileShareGroupEntity.getUserId());
				throw new BizException(BizMessage.FILE_SHARE_OWNER_ERROR);
			}
			if (!isValidGroup(fileShareGroupEntity)){
				log.warn("用户[{}]操作的分享文件组[{}]已失效", userId, fileShareGroupEntity.getId());
				throw new BizException(BizMessage.FILE_SHARE_INVALID);
			}
			fileShareGroupEntity.setActive(false);
		});
		fileShareGroupRepository.saveAll(shareGroupEntityList);
	}

	/**
	 * 保存分享文件
	 *  @param shareIds
	 * @param userId
	 */
	@Override
	public void saveShareFile(List<String> shareIds, String pid, Long userId) {
		if (Objects.nonNull(pid)){
			userFileRepository.findById(pid).ifPresent(uf->{
				if (!uf.getUserId().equals(userId)){
					log.warn("非法操作, 用户[{}]保存文件到他人目录[{}]", userId, pid);
					throw new BizException(BizMessage.USER_FILE_NOT_ACCESS);
				}
				if (!uf.getDir()){
					log.warn("[{}]不是一个正确的目录", pid);
					throw new BizException(BizMessage.USER_FILE_NOT_DIR);
				}
			});
		}

		Set<String> shareGroupIdSet = fileShareRepository.findAllById(shareIds).stream().map(FileShareEntity::getFileShareGroupId).collect(Collectors.toSet());
		List<FileShareGroupEntity> shareGroupEntityList = fileShareGroupRepository.findAllById(shareGroupIdSet);
		shareGroupEntityList.stream().filter(fileShareGroupEntity -> !isValidGroup(fileShareGroupEntity)).findFirst().ifPresent(fileShareGroupEntity -> {
			log.warn("用户[{}]保存失效的分享[{}]", userId, fileShareGroupEntity);
			throw new BizException(BizMessage.FILE_SHARE_INVALID);
		});

		// 组下的所有分享文件
		List<FileShareEntity> allShareFiles = fileShareRepository.findAllByFileShareGroupIdIn(shareGroupIdSet);
		Map<String, FileShareEntity> shareFileMap = allShareFiles.stream().collect(Collectors.toMap(FileShareEntity::getShareId, fse -> fse));
		Set<String> userFileIds = allShareFiles.stream().map(FileShareEntity::getUserFileId).collect(Collectors.toSet());
		List<UserFileEntity> userFileEntityList = userFileRepository.findAllById(userFileIds);
		Map<String, UserFileEntity> userFileMap = userFileEntityList.stream().collect(Collectors.toMap(UserFileEntity::getId, uf -> uf));

		List<String> fileIds = userFileEntityList.stream().filter(uf->!uf.getDir()&&!uf.getDelete()).map(UserFileEntity::getFileId).collect(Collectors.toList());
		Map<String, FileEntity> fileMap = fileRepository.findAllById(fileIds).stream().collect(Collectors.toMap(FileEntity::getId, fe -> fe));

		// 清除失效分享
		List<FileShareEntity> deletedShareFile = allShareFiles.stream().filter(fileShareEntity -> Objects.isNull(userFileMap.get(fileShareEntity.getUserFileId())) || userFileMap.get(fileShareEntity.getUserFileId()).getDelete()).collect(Collectors.toList());
		if (!deletedShareFile.isEmpty()){
			List<FileShareEntity> deletedChildren = new LinkedList<>();
			// 查找失效分享的子文件
			deletedShareFile.forEach(fse->{
				if (fse.getDir()){
					addChildrenToList(fse.getShareId(), allShareFiles, deletedChildren);
				}
			});
			log.info("用户[{}]删除失效分享文件{}", userId, deletedShareFile);
			deletedShareFile.addAll(deletedChildren);
			allShareFiles.removeAll(deletedShareFile);
			fileShareRepository.deleteAll(deletedShareFile);
		}

		// 需要保存的分享文件
		LinkedList<FileShareEntity> toSaveShareFiles = new LinkedList<>();
		shareIds.forEach(id->{
			FileShareEntity fileShareEntity = shareFileMap.get(id);
			toSaveShareFiles.add(fileShareEntity);
			addChildrenToList(id, allShareFiles, toSaveShareFiles);
		});

		List<UserFileEntity> newUserFileList = toSaveShareFiles.stream().map(fse->{
			UserFileEntity fromUserFile = userFileMap.get(fse.getUserFileId());
			UserFileEntity toUserFile = new UserFileEntity();

			BeanUtils.copyProperties(fromUserFile, toUserFile);
			toUserFile.setFilename(fse.getFilename());
			toUserFile.setFilename(fse.getExtension());
			toUserFile.setUserId(userId);
			toUserFile.setId(fse.getShareId());
			toUserFile.setPid(fse.getPid());
			return toUserFile;
		}).collect(Collectors.toList());

		newUserFileList.forEach(userFileEntity -> {
			String id = UUIDUtil.random32();
			if (shareIds.contains(userFileEntity.getId())){
				userFileEntity.setPid(null);
			}
			if (userFileEntity.getDir()){
				newUserFileList.stream().filter(uf->userFileEntity.getId().equals(uf.getPid())).forEach(uf->{
					uf.setPid(id);
				});
			}else{
				FileEntity fileEntity = fileMap.get(userFileEntity.getFileId());
				fileService.increaseCount(fileEntity);
				userStorageService.increaseStorage(fileEntity.getSize());
			}
			userFileEntity.setId(id);
		});
	}

	/**
	 * 将分享文件id为{@code id}的子文件全部添加到{@code to}列表中
	 * @param id
	 * @param all
	 * @param to
	 */
	private void addChildrenToList(String id, List<FileShareEntity> all, List<FileShareEntity> to){
		all.stream().filter(fse->id.equals(fse.getPid())).forEach(fse->{
			to.add(fse);
			if (fse.getDir()){
				addChildrenToList(fse.getShareId(), all, to);
			}
		});
	}
	/**
	 * 保存次数+1
	 * @return
	 */
	@Override
	public void sCountPlusOne(FileShareEntity fileShareEntity){
		fileShareEntity.setSCount(fileShareEntity.getSCount()+1L);
	}

	/**
	 * 下载次数+1
	 * @return
	 */
	@Override
	public void dCountPlusOne(FileShareEntity fileShareEntity){
		fileShareEntity.setDCount(fileShareEntity.getDCount()+1L);
	}

	/**
	 * 如果该分享组未失效返回true，否则返回false
	 * @param fileShareGroupEntity
	 * @return
	 */
	private boolean isValidGroup(FileShareGroupEntity fileShareGroupEntity){
		return fileShareGroupEntity.getActive() && SDateUtils.nowBefore(fileShareGroupEntity.getDueDate());
	}
}
