package io.ddisk.utils;

import io.ddisk.domain.enums.ThumbnailTypeEnum;
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
	 * 获取切片文件根目录
	 * @param identifier
	 * @return
	 */
	public static Path getChunkRootPath(String identifier){
		return Path.of(UPLOAD_FOLDER, CHUNK_PATH, identifier);
	}

	/**
	 * 获取文件切片目录
	 * @param identifier
	 * @return
	 */
	public static Path getChunkDirPath(String identifier, Long chunkSize){
		return Path.of(getChunkRootPath(identifier).toString(), String.valueOf(chunkSize));
	}

	/**
	 * 拿到切片文件路径
	 * @return
	 */
	public static Path getChunkFilePath(String fileId, Long chunkSize, Integer chunkNumber) {
		Path path = Path.of(getChunkDirPath(fileId, chunkSize).toString(), String.valueOf(chunkNumber));
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
	 * 获取略缩图文件路径
	 */
	public static Path getThumbnailFilePath(String identifier, String extension, ThumbnailTypeEnum type){

		String filename = String.format("%s%s.%s", identifier, type.getSuffixName(), extension);
		Path path = Path.of(UPLOAD_FOLDER, THUMBNAIL_PATH, filename);
		return FileUtils.mkdirs(path, false);
	}

}
