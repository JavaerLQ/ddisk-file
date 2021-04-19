package io.ddisk.service.impl;

import io.ddisk.dao.FileRepository;
import io.ddisk.dao.UserFileRepository;
import io.ddisk.domain.dto.PageDTO;
import io.ddisk.domain.entity.FileEntity;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.enums.FileTypeEnum;
import io.ddisk.domain.vo.DirTreeNode;
import io.ddisk.domain.vo.FileVO;
import io.ddisk.domain.vo.PathNodeVO;
import io.ddisk.domain.vo.base.PageVO;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.service.UserFileService;
import io.ddisk.service.UserStorageService;
import io.ddisk.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/25
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserFileServiceImpl implements UserFileService {

	@Autowired
	private UserFileRepository userFileRepository;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private UserStorageService userStorageService;


	/**
	 * 创建文件夹
	 *
	 * @param userId
	 * @param pid      父目录
	 * @param filename 文件夹
	 */
	@Override
	public void mkdir(Long userId, Long pid, String filename) {
		userFileRepository.save(new UserFileEntity(userId, filename, pid));
	}

	/**
	 * 文件重命名
	 *
	 * @param userFileId 用户文件id
	 * @param filename   文件名
	 * @param extension  扩展名
	 */
	@Override
	public void rename(Long userFileId, String filename, String extension) {
		UserFileEntity userFileEntity = userFileRepository.findById(userFileId).orElseThrow(() -> new BizException(BizMessage.USER_FILE_NOT_EXIST));
		if (Objects.nonNull(filename)) {
			userFileEntity.setFilename(filename);
		}
		if (Objects.nonNull(extension)) {
			userFileEntity.setExtension(extension);
		}
		userFileRepository.save(userFileEntity);
	}

	/**
	 * 分页查询 列出某目录下的所有文件，包含文件夹
	 *
	 * @param userId 用户ID
	 * @param pid    父目录ID
	 * @param page
	 * @return
	 */
	@Override
	public PageVO<FileVO> listTheDir(Long userId, Long pid, PageDTO page) {

		Page<UserFileEntity> userFilePage = userFileRepository.findAllByUserIdAndPidAndDelete(userId, pid, false, page.buildPageRequest());

		return pageToVO(userFilePage);
	}

	/**
	 * 确认目标文件夹是否有效
	 * @param userFileId
	 */
	private void checkTargetDir(Long userFileId){
		if (Objects.nonNull(userFileId)){
			UserFileEntity toFile = userFileRepository.findByIdAndDelete(userFileId, false).orElseThrow(() -> new BizException(BizMessage.USER_DIR_NOT_EXIST));
			if (!toFile.getDir()) {
				throw new BizException(BizMessage.USER_FILE_NOT_DIR);
			}
		}
	}
	/**
	 * 移动文件
	 *
	 * @param from
	 * @param to
	 */
	@Override
	public void move(Long from, Long to) {
		checkTargetDir(to);
		UserFileEntity fromFile = userFileRepository.findByIdAndDelete(from, false).orElseThrow(() -> new BizException(BizMessage.USER_DIR_NOT_EXIST));
		fromFile.setPid(to);
		userFileRepository.save(fromFile);
	}

	/**
	 * 批量移动文件
	 *
	 * @param fromList
	 * @param to
	 */
	@Override
	public void move(List<Long> fromList, Long to) {
		checkTargetDir(to);
		userFileRepository.updatePidByIdInAndPid(fromList, to);
	}

	/**
	 * 删除文件或者文件夹，删除文件夹时，文件夹里的文件也一并删除。此操作为逻辑删除
	 *
	 * @param userFileId
	 */
	@Override
	public void delete(Long userFileId) {
		deleteOrRecover(List.of(userFileId), true);
	}

	/**
	 * 批量删除文件或者文件夹，删除文件夹时，文件夹里的文件也一并删除。此操作为逻辑删除
	 *
	 * @param userFileList
	 */
	@Override
	public void delete(List<Long> userFileList) {
		deleteOrRecover(userFileList, true);
	}

	@Override
	public void recover(Long userFileId) {
		deleteOrRecover(List.of(userFileId), false);
	}

	/**
	 * 批量恢复回收站文件
	 *
	 * @param userFileIdList
	 */
	@Override
	public void recover(List<Long> userFileIdList) {
		deleteOrRecover(userFileIdList, false);
	}

	/**
	 * 真正的删除，从回收站里删除
	 *
	 * @param userFileId
	 */
	@Override
	public void deleteFromRecycleBin(Long userFileId) {
		deleteFromRecycleBin(List.of(userFileId));
	}

	/**
	 * 真正的删除，从回收站里删除
	 *
	 * @param userFileIdList
	 */
	@Override
	public void deleteFromRecycleBin(List<Long> userFileIdList) {
		List<UserFileEntity> childrenList = getChildrenList(userFileIdList, true);
		userFileRepository.deleteAll(childrenList);
	}

	/**
	 * 分页查询出这个类型的文件
	 *
	 * @param fileType
	 * @param page
	 * @return
	 */
	@Override
	public PageVO<FileVO> listTheTypeFile(Long userId, FileTypeEnum fileType, PageDTO page) {

		Page<UserFileEntity> userFilePage = null;
		Set<String> extensions = FileUtils.getFileExtensionsByType(fileType);

		if (FileTypeEnum.OTHER == fileType) {
			userFilePage = userFileRepository.findAllByUserIdAndExtensionNotInAndDirIsFalseAndDeleteIsFalse(userId, extensions, page.buildPageRequest());
		} else {
			userFilePage = userFileRepository.findAllByUserIdAndExtensionInAndDirIsFalseAndDeleteIsFalse(userId, extensions, page.buildPageRequest());
		}
		return pageToVO(userFilePage);
	}

	/**
	 * 列出回收站所有文件
	 *
	 * @param userId
	 * @param page
	 * @return
	 */
	@Override
	public PageVO<FileVO> listRecycle(Long userId, PageDTO page) {
		Page<UserFileEntity> userFilePage = userFileRepository.findAllRecycleRoot(userId, page.buildPageRequest());
		return pageToVO(userFilePage);
	}


	/**
	 * 批量删除或恢复文件
	 *
	 * @param userFileIdList 文件或目录id列表
	 * @param delete     删除文件true, 恢复文件false
	 */
	private void deleteOrRecover(List<Long> userFileIdList, Boolean delete) {

		List<UserFileEntity> childrenList = getChildrenList(userFileIdList, !delete);
		userStorageService.deleteFileCalculator(childrenList, delete);
		userFileRepository.updateDeleteByIdIn(childrenList.stream().map(UserFileEntity::getId).collect(Collectors.toList()), delete);
	}

	/**
	 * 获取这些文件或者目录及子文件、子目录
	 *
	 * @param userFileIdList
	 * @param delete     如果是获取回收站文件及子文件true，获取非回收站文件false
	 * @return
	 */
	private List<UserFileEntity> getChildrenList(List<Long> userFileIdList, Boolean delete) {

		List<UserFileEntity> userFileList = userFileRepository.findAllByIdInAndDelete(userFileIdList, delete);
		List<UserFileEntity> result = new LinkedList<>(userFileList);

		userFileList.forEach(userFileEntity -> {
			if (userFileEntity.getDir()) {
				List<Long> tmpIds = List.of(userFileEntity.getId());
				while (!CollectionUtils.isEmpty(tmpIds)) {
					List<UserFileEntity> children = userFileRepository.findAllByPidInAndDelete(tmpIds, delete);
					tmpIds = children.parallelStream().map(UserFileEntity::getId).collect(Collectors.toList());
					result.addAll(children);
				}
			}
		});
		return result;
	}

	/**
	 * 展示目录树
	 *
	 * @param userId
	 * @return
	 */
	@Override
	public DirTreeNode dirTree(Long userId) {

		List<UserFileEntity> userFileList = userFileRepository.findAllByUserIdAndDirAndDelete(userId, true, false);
		DirTreeNode rootNode = DirTreeNode.root();

		rootNode.setChildren(
				userFileList.parallelStream().filter(uf -> Objects.isNull(uf.getPid())).map(uf ->
						DirTreeNode.create(uf, 1L, getDirChildrenTree(uf.getId(), 2L, userFileList))
				).collect(Collectors.toList())
		);
		return rootNode;
	}

	/**
	 * 递归获取子目录
	 *
	 * @param pid
	 * @param depth
	 * @param userFileEntityList
	 * @return
	 */
	private List<DirTreeNode> getDirChildrenTree(Long pid, Long depth, List<UserFileEntity> userFileEntityList) {

		return userFileEntityList.parallelStream().filter(uf -> pid.equals(uf.getPid())).map(uf ->
				DirTreeNode.create(uf, depth, getDirChildrenTree(uf.getId(), depth + 1, userFileEntityList))
		).collect(Collectors.toList());
	}

	/**
	 * 数据库查询出来的UserFileEntity Page对象转成PageVO对象
	 *
	 * @param userFilePage
	 * @return
	 */
	private PageVO<FileVO> pageToVO(Page<UserFileEntity> userFilePage) {
		// 查找真正的文件信息类
		Set<String> fileUuidSet = userFilePage.get().parallel().map(UserFileEntity::getFileId).collect(Collectors.toSet());
		Map<String, Long> fileSizeMap = fileRepository.findAllById(fileUuidSet).stream().parallel().collect(Collectors.toMap(FileEntity::getId, FileEntity::getSize));

		List<FileVO> fileVOList = userFilePage.get().map(uf -> FileVO.create(uf, fileSizeMap.get(uf.getFileId()))).collect(Collectors.toList());

		return new PageVO<>(userFilePage.getTotalElements(), fileVOList);
	}

	/**
	 * 获取路径树map
	 *
	 * @param userId
	 * @return
	 */
	
	@Override
	public Map<Long, PathNodeVO> getPathTreeMap(Long userId) {

		return userFileRepository.findAllPathNodeByUserId(userId).stream().collect(Collectors.toMap(PathNodeVO::getId, PathNodeVO::self));
	}

	/**
	 * 搜索文件
	 *
	 * @param filename
	 * @param page
	 * @return
	 */
	@Override
	public PageVO<FileVO> searchFile(String filename, PageDTO page) {

		Page<UserFileEntity> userFileEntityPage = userFileRepository.findAllByFilenameLike("%"+filename+"%", page.buildPageRequest());
		return pageToVO(userFileEntityPage);
	}

	/**
	 * 检查图片是否合法，是否是该用户的图片
	 *
	 * @param userId
	 * @param fileId
	 */
	@Override
	public void checkImage(Long userId, Long fileId) {
		UserFileEntity userFileEntity = userFileRepository.findById(fileId).orElseThrow(() -> new BizException(BizMessage.USER_FILE_NOT_EXIST));
		if (!userFileEntity.getUserId().equals(userId)){
			throw new BizException(BizMessage.FILE_ILLEGAL_ACCESS);
		}
		FileEntity fileEntity = fileRepository.findById(userFileEntity.getFileId()).orElseThrow(() -> new BizException(BizMessage.FILE_NOT_EXIST));
		String mimetype = fileEntity.getMimetype().toLowerCase(Locale.ROOT);
		if (!mimetype.startsWith("image")){
			throw new BizException(BizMessage.FILE_NOT_IMAGE);
		}
	}
}
