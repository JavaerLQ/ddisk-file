package io.ddisk.service.impl;

import io.ddisk.dao.*;
import io.ddisk.domain.entity.FileEntity;
import io.ddisk.domain.entity.ThumbnailEntity;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.entity.UserStorageEntity;
import io.ddisk.service.FileCleanerService;
import io.ddisk.utils.FileUtils;
import io.ddisk.utils.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/6
 */
@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class FileCleanerServiceImpl implements FileCleanerService {

	@Autowired
	private ChunkRepository chunkRepository;
	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ThumbnailRepository thumbnailRepository;
	@Autowired
	private UserFileRepository userFileRepository;
	@Autowired
	private UserStorageRepository userStorageRepository;

	/**
	 * 清理已合并文件
	 */
	@Override
	public void cleanMergedFiles() {
		List<String> mergedFileId = chunkRepository.findMergedFiles();
		mergedFileId.forEach(fileId-> FileUtils.deleteRecursively(PathUtils.getChunkDirPath(fileId)));
		chunkRepository.deleteAllByIdentifierIn(mergedFileId);
		log.info("垃圾回收器清理{}个文件残余切片", mergedFileId.size());
	}

	/**
	 * 清理未完成的切片
	 */
	@Override
	public void cleanIncompleteChunks() {
		// 默认清理1周前的碎片
		Date date = Date.from(LocalDate.now().minusWeeks(1L).atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<String> fileIds = chunkRepository.findIncompleteChunksDateBefore(date);
		fileIds.forEach(id->FileUtils.deleteRecursively(PathUtils.getChunkDirPath(id)));
		chunkRepository.deleteAllByIdentifierIn(fileIds);
		log.info("垃圾回收器清理{}未完成合并文件切片", fileIds.size());
	}

	/**
	 * 清理count为0的文件
	 */
	@Override
	public void cleanCount0Files() {
		// 默认清理2周前的未使用的文件
		Date date = Date.from(LocalDate.now().minusWeeks(2L).atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<FileEntity> fileList = fileRepository.findAllByCountAndCreateTimeBefore(0L, date);
		fileList.forEach(fileEntity -> FileUtils.deleteRecursively(Path.of(fileEntity.getUrl())));
		fileRepository.deleteAll(fileList);
		log.info("垃圾回收器清理{}个无人引用的文件", fileList.size());
	}

	/**
	 * 清理缩略图垃圾文件
	 */
	@Override
	public void cleanThumbnail() {

		List<ThumbnailEntity> garbageList = thumbnailRepository.findAllGarbage();
		garbageList.forEach(thumbnailEntity -> FileUtils.deleteRecursively(Path.of(thumbnailEntity.getUrl())));
		thumbnailRepository.deleteAll(garbageList);
		log.info("垃圾回收器清理{}个残留缩略图", garbageList.size());
	}

	/**
	 * 清理回收站文件
	 */
	@Override
	public void cleanRecycleFile() {
		// 回收站文件默认保存一个月
		Date date = Date.from(LocalDate.now().minusMonths(1L).atStartOfDay(ZoneId.systemDefault()).toInstant());
		// 到期的回收站文件
		List<UserFileEntity> userFileList = userFileRepository.findAllByDeleteIsTrueAndDeleteTimeBefore(date);
		// 删除用户文件 指向的真实文件，需要删除其引用数，提供文件大小，用于减少用户容量
		Set<String> uuidSet = userFileList.parallelStream().filter(uf->!uf.getDir()).map(UserFileEntity::getFileId).collect(Collectors.toSet());
		Map<String, FileEntity> fileMap = fileRepository.findAllById(uuidSet).stream().collect(Collectors.toMap(FileEntity::getId, fileEntity -> fileEntity));
		// 用户存储
		List<Long> userIds = userFileList.stream().map(UserFileEntity::getUserId).collect(Collectors.toList());
		Map<Long, UserStorageEntity> userStorageMap = userStorageRepository.findAllByUserIdIn(userIds).stream().collect(Collectors.toMap(UserStorageEntity::getUserId, us -> us));


		userFileList.forEach(uf->{
			if (!uf.getDir()){
				// 删除真实文件引用数
				FileEntity file = fileMap.get(uf.getFileId());
				file.setCount(file.getCount()-1);
				// 减少用户存储空间
				UserStorageEntity us = userStorageMap.get(uf.getUserId());
				us.setUsedStorage(us.getUsedStorage()-file.getSize());
			}
		});
		userFileRepository.deleteAll(userFileList);
		fileRepository.saveAll(fileMap.values());
		userStorageRepository.saveAll(userStorageMap.values());
		log.info("垃圾回收器清理回收站文件{}个: \n{}", userFileList.size(), userFileList);
	}
}
