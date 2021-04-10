package io.ddisk.service.impl;

import io.ddisk.dao.ChunkRepository;
import io.ddisk.dao.FileRepository;
import io.ddisk.dao.ThumbnailRepository;
import io.ddisk.dao.UserFileRepository;
import io.ddisk.domain.consts.FileConst;
import io.ddisk.domain.dto.FileDTO;
import io.ddisk.domain.dto.FileUploadDTO;
import io.ddisk.domain.dto.MergeFileDTO;
import io.ddisk.domain.entity.ChunkEntity;
import io.ddisk.domain.entity.FileEntity;
import io.ddisk.domain.entity.ThumbnailEntity;
import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.enums.ImageSizeEnum;
import io.ddisk.domain.enums.RoleEnum;
import io.ddisk.domain.vo.UploadFileVO;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.service.FileService;
import io.ddisk.service.UserStorageService;
import io.ddisk.utils.FileUtils;
import io.ddisk.utils.ImageUtils;
import io.ddisk.utils.SpringWebUtils;
import jodd.io.FileNameUtil;
import jodd.net.MimeTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/8
 */
@Service
@Slf4j
@Transactional(rollbackFor = Throwable.class)
public class FileServiceImpl implements FileService {

	@Autowired
	private FileRepository fileRepository;
	@Autowired
	private ChunkRepository chunkRepository;
	@Autowired
	private UserFileRepository userFileRepository;
	@Autowired
	private UserStorageService userStorageService;
	@Autowired
	private ThumbnailRepository thumbnailRepository;

	/**
	 * 如果一个文件存在，则跳过上传直接保存数据库
	 *
	 * @return
	 */
	@Override
	public UploadFileVO speedUpload(Long userId, FileUploadDTO fileUploadDTO) {

		userStorageService.hasSize(fileUploadDTO.getFileSize());
		FileEntity fileEntity = fileRepository.findById(fileUploadDTO.getIdentifier()).orElse(null);

		// 如果存在，保存到该用户的用户文件夹
		if (Objects.nonNull(fileEntity)) {

			if (Objects.isNull(fileUploadDTO.getExtension())){
				fileUploadDTO.setExtension(FileNameUtil.getExtension(fileUploadDTO.getFilename()));
				fileUploadDTO.setFilename(FileNameUtil.getBaseName(fileUploadDTO.getFilename()));
			}
			userFileRepository.save(new UserFileEntity(userId, fileUploadDTO.getFilename(), fileUploadDTO.getExtension(), fileUploadDTO.getFolderId(), fileEntity.getId()));
			userStorageService.increaseStorage(SpringWebUtils.requireLogin(), fileEntity.getSize());
			increaseCount(fileEntity);
		}

		return UploadFileVO.builder().skipUpload(Objects.nonNull(fileEntity)).build();
	}

	/**
	 * 文件上传
	 *
	 * @param userId
	 * @param fileUploadDTO
	 * @return 返回已上传过的切片
	 */
	@Override
	public Set<Integer> upload(Long userId, FileUploadDTO fileUploadDTO) {

		userStorageService.hasSize(fileUploadDTO.getFileSize());
		List<ChunkEntity> chunks = chunkRepository.findAllByIdentifier(fileUploadDTO.getIdentifier());
		Set<Integer> uploaded = chunks.parallelStream().map(ChunkEntity::getChunkNumber).collect(Collectors.toSet());
		// 该切片已上传过
		if (uploaded.contains(fileUploadDTO.getChunkNumber())) {
			return uploaded;
		}
		// 将切片文件上传至本地磁盘
		FileUtils.chunk(fileUploadDTO);

		ChunkEntity chunkEntity = new ChunkEntity();
		BeanUtils.copyProperties(fileUploadDTO, chunkEntity, chunkEntity.getClass());

		chunkRepository.save(chunkEntity);
		uploaded = chunkRepository.findAllNumberByIdentifier(fileUploadDTO.getIdentifier());

		if (uploaded.size() == fileUploadDTO.getTotalChunks()){
			log.info("[{}]完成[{}]文件最后一块切片上传", SpringWebUtils.requireLogin().getUsername(), fileUploadDTO.getIdentifier());
		}
		return uploaded;
	}

	/**
	 * 合并已上传切片
	 *
	 * @param mergeFileDTO
	 */
	@Override
	public FileEntity mergeFile(MergeFileDTO mergeFileDTO) {

		// 无上传切片
		List<ChunkEntity> chunks = chunkRepository.findAllByIdentifier(mergeFileDTO.getIdentifier());
		if (CollectionUtils.isEmpty(chunks)) {
			throw new BizException(BizMessage.CHUNK_INCOMPLETE);
		}

		// 切片不完整
		Set<Integer> uploaded = chunks.parallelStream().map(ChunkEntity::getChunkNumber).collect(Collectors.toSet());
		Integer totalChunks = chunks.get(0).getTotalChunks();
		if (!totalChunks.equals(uploaded.size())) {
			throw new BizException(BizMessage.CHUNK_INCOMPLETE);
		}

		// 合并后文件路径
		Path fromPath = FileUtils.mergeFile(
				FileUtils.getChunkFolder(chunks.get(0).getIdentifier()),
				chunks.get(0).getTotalChunks()
		);

		// 获取文件基本信息
		String md5 = FileUtils.md5(fromPath);
		String mimetype = FileUtils.mimetype(fromPath, mergeFileDTO.getFilename());
		Path toPath = FileUtils.getFilePath(mimetype, md5);

		// 与前端提供的md5不一致，代表合并失败
		if (!md5.equals(mergeFileDTO.getIdentifier())) {
			FileUtils.deleteRecursively(fromPath);
			throw new BizException(BizMessage.FILE_MERGE_FAIL);
		}

		// 移动文件
		FileUtils.move(fromPath, toPath);

		return fileRepository.save(new FileEntity(md5, FileUtils.size(toPath), toPath.toString(), 0L, mimetype));
	}

	/**
	 * 增加引用，实体类状态必须为持久化状态
	 *
	 * @param fileEntity
	 */
	@Override
	public void increaseCount(FileEntity fileEntity) {
		fileEntity.setCount(fileEntity.getCount() + 1);
		fileRepository.save(fileEntity);
	}

	/**
	 * 增加引用
	 *
	 * @param fileIdentity 文件唯一表示
	 */
	@Override
	public void increaseCount(String fileIdentity) {
		FileEntity fileEntity = fileRepository.findById(fileIdentity).orElse(null);
		if (Objects.nonNull(fileEntity)) {
			fileEntity.setCount(fileEntity.getCount() + 1);
			fileRepository.save(fileEntity);
		}
	}

	/**
	 * 减少引用数，实体类状态必须为持久化状态
	 *
	 * @param fileEntity
	 */
	@Override
	public void decreaseCount(FileEntity fileEntity) {
		Long count = fileEntity.getCount();
		if (count > 0) {
			fileEntity.setCount(count - 1);
			fileRepository.save(fileEntity);
		}
	}

	/**
	 * 减少引用数
	 *
	 * @param fileIdentity
	 */
	@Override
	public void decreaseCount(String fileIdentity) {
		FileEntity fileEntity = fileRepository.findById(fileIdentity).orElse(null);
		if (Objects.nonNull(fileEntity) && fileEntity.getCount() > 0) {
			fileEntity.setCount(fileEntity.getCount() - 1);
			fileRepository.save(fileEntity);
		}
	}

	/**
	 * 文件下载，管理员可下载任意用户的文件
	 *
	 * @param userId
	 * @param role
	 * @param userFileId
	 * @return 返回文件下载信息
	 */
	@Override
	public FileDTO getFileResource(Long userId, RoleEnum role, Long userFileId) {

		UserFileEntity userFileEntity = userFileRepository.findById(userFileId)
				.map(uf -> {
					if (uf.getDir()) {
						throw new BizException(BizMessage.DIR_CAN_NOT_DOWNLOAD);
					}
					if (userId.equals(uf.getUserId()) || uf.getShare() || RoleEnum.ADMIN.equals(role)) {
						return uf;
					}
					throw new BizException(BizMessage.FILE_ILLEGAL_ACCESS);
				})
				.orElseThrow(() -> new BizException(BizMessage.FILE_NOT_EXIST));

		FileEntity fileEntity = fileRepository.findById(userFileEntity.getFileId()).orElseThrow(() -> new BizException(BizMessage.FILE_NOT_EXIST));

		String mimetype = MimeTypes.getMimeType(userFileEntity.getExtension());
		mimetype = mimetype.equals(MimeTypes.MIME_APPLICATION_OCTET_STREAM) ? fileEntity.getMimetype() : mimetype;

		return new FileDTO(userFileEntity.getFilename(), userFileEntity.getExtension(), mimetype, fileEntity.getUrl(), fileEntity.getSize());
	}

	/**
	 * 懒加载获取略缩图
	 * 先查询数据库是否有该略缩图，如果有，直接生成FileDTO对象，如果无，则生成。
	 * @param userFileId
	 * @return
	 */
	@Override
	public FileDTO getThumbnail(Long userId, Long userFileId) {

		UserFileEntity userFileEntity = userFileRepository.findById(userFileId)
				.map(uf -> {
					if (uf.getDir()) {
						throw new BizException(BizMessage.DIR_CAN_NOT_DOWNLOAD);
					}
					if (!uf.getUserId().equals(userId)){
						throw new BizException(BizMessage.USER_FILE_NOT_ACCESS);
					}
					if (!FileConst.IMG_FILE.contains(uf.getExtension())){
						throw new BizException(BizMessage.FILE_NOT_IMAGE);
					}
					return uf;
				})
				.orElseThrow(() -> new BizException(BizMessage.FILE_NOT_EXIST));

		ThumbnailEntity thumbnail = thumbnailRepository.findByFileIdAndImageSize(userFileEntity.getFileId(), ImageSizeEnum.MIN).orElseGet(() -> {
			FileEntity fileEntity = fileRepository.findById(userFileEntity.getFileId()).orElseThrow(() -> new BizException(BizMessage.FILE_NOT_EXIST));
			Path out = FileUtils.getThumbnailPath(fileEntity.getId(), userFileEntity.getExtension(), ImageSizeEnum.MIN);
			ImageUtils.generateMin(new File(fileEntity.getUrl()), out.toFile());
			ThumbnailEntity thumbnailEntity = new ThumbnailEntity(fileEntity.getId(), out.toString(), ImageSizeEnum.MIN, FileUtils.size(out));
			return thumbnailRepository.save(thumbnailEntity);
		});

		return new FileDTO(
				FileNameUtil.getName(thumbnail.getUrl()),
				MimeTypes.getMimeType(userFileEntity.getExtension()),
				thumbnail.getUrl()
		);
	}
}
