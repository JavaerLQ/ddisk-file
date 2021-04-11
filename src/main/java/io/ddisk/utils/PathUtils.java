package io.ddisk.utils;

import io.ddisk.domain.dto.FileUploadDTO;
import io.ddisk.domain.enums.ImageSizeEnum;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;

import static io.ddisk.domain.consts.FileConst.*;

/**
 * 路径操作工具类
 * @Author: Richard.Lee
 * @Date: created by 2021/4/11
 */
@Slf4j
public class PathUtils {

	/**
	 * 获取文件切片目录
	 * @param identifier
	 * @return
	 */
	public static Path getChunkDirPath(String identifier){
		return Path.of(UPLOAD_FOLDER, CHUNK_PATH, identifier);
	}

	/**
	 * 拿到切片文件路径
	 *
	 * @param fileUploadDTO
	 * @return
	 */
	public static Path getChunkFilePath(FileUploadDTO fileUploadDTO) {

		Path path = Path.of(
				UPLOAD_FOLDER,
				CHUNK_PATH,
				fileUploadDTO.getIdentifier(),
				fileUploadDTO.getChunkNumber().toString()
		);
		return FileUtils.mkdirs(path, false);
	}

	/**
	 * 获取文件路径，文件路径规则：文件上传路径/文件类型/文件唯一标识
	 * @return
	 */
	public static Path getFilePath(String mimetype, String identifier) {

		Path path = Path.of(UPLOAD_FOLDER, mimetype, identifier);
		return FileUtils.mkdirs(path, false);
	}


	/**
	 * 获取略缩图文件基础文件名
	 */
	public static Path getThumbnailBaseName(String identifier, ImageSizeEnum sizeEnum){
		String filename = String.format("%s_%dx%d", identifier, sizeEnum.getWidth(), sizeEnum.getHeight());
		return Path.of(UPLOAD_FOLDER, UPLOAD_FOLDER, filename);
	}

	/**
	 * 获取略缩图文件路径
	 */
	public static Path getThumbnailFilePath(String identifier, String extension, ImageSizeEnum sizeEnum){
		String filename = String.format("%s_%dx%d.%s", identifier, sizeEnum.getWidth(), sizeEnum.getHeight(), extension);
		Path path = Path.of(UPLOAD_FOLDER, THUMBNAIL_PATH, filename);
		return FileUtils.mkdirs(path, false);
	}

}
