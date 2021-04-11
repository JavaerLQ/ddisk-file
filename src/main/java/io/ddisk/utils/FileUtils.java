package io.ddisk.utils;

import io.ddisk.domain.dto.FileUploadDTO;
import io.ddisk.domain.enums.FileTypeEnum;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.vavr.control.Try;
import jodd.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;

import javax.activation.MimetypesFileTypeMap;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static io.ddisk.domain.consts.FileConst.*;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/17
 */
@Slf4j
public class FileUtils {

	/**
	 * 切片上传，切片文件命名规则：
	 * 文件上传路径/chunk/文件唯一标识/第几切片
	 *
	 * @param fileUploadDTO
	 */
	public static void chunk(FileUploadDTO fileUploadDTO) {

		Try.withResources(
				() -> Optional.ofNullable(fileUploadDTO.getFile())
						.map(file -> Try.of(file::getInputStream).getOrElseThrow(() -> new BizException(BizMessage.UPLOAD_FILE_STREAM_FAIL)))
						.orElseThrow(() -> new BizException(BizMessage.UPLOAD_FILE_NULL)),
				() -> new FileOutputStream(PathUtils.getChunkFilePath(fileUploadDTO).toFile())
		).of(FileCopyUtils::copy).getOrElseThrow(() -> new BizException(BizMessage.FILE_COPY_FAIL));
	}

	/**
	 * 合并切片
	 * @param chunkPath 切片目录
	 * @param chunkTotal 切片数量
	 * @return
	 */
	public static Path mergeFile(Path chunkPath, Integer chunkTotal){

		String cps = chunkPath.toString();
		Path outPath = Path.of(cps, FILE_TMP);
		if (FileUtil.isExistingFile(outPath.toFile())){
			return outPath;
		}
		Try.of(() -> Files.createFile(Path.of(cps, FILE_TMP))).getOrElseThrow(() -> new BizException(BizMessage.FILE_CREATE_FILE));
		for (int i = 1; i <= chunkTotal; i++) {
			Path chunkFile = Path.of(cps, String.valueOf(i));
			byte[] bytes = Try.of(() -> Files.readAllBytes(chunkFile)).getOrElseThrow(() -> new BizException(BizMessage.UPLOAD_FILE_GET_BYTES_FAIL));
			Try.run(()->Files.write(outPath, bytes, StandardOpenOption.APPEND));
		}
		log.info("合并{}个切片文件[{}]", chunkTotal, chunkPath.getFileName());
		return outPath;
	}

	/**
	 * 创建失败抛出异常，存在或者创建成功返回路径
	 *
	 * @param path
	 * @param isDir 是否是目录
	 * @return
	 */
	public static Path mkdirs(Path path, boolean isDir) {

		Path tmp = isDir ? path : path.getParent();
		if (Files.notExists(tmp)) {
			Try.of(() -> Files.createDirectories(tmp)).getOrElseThrow(() -> {
				log.error("目录创建失败[{}]", tmp);
				throw new BizException(BizMessage.DIR_CREATE_FAIL);
			});
			log.info("创建目录[{}]", tmp.getFileName());
		}
		return path;
	}

	/**
	 * 递归删除
	 *
	 * @param path
	 * @return
	 */
	public static boolean deleteRecursively(Path path) {
		log.info("删除文件[{}]", path.getFileName());
		return Try.of(() -> FileSystemUtils.deleteRecursively(path)).getOrElseThrow(() -> new BizException(BizMessage.FILE_DELETE_RECURSIVELY));
	}

	/**
	 * 获取一个文件的md5值(可处理大文件)
	 * @return md5 value
	 */
	public static String md5(Path filePath) {

		try(FileInputStream fileInputStream = new FileInputStream(filePath.toFile())) {
			MessageDigest MD5 = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[Math.toIntExact(CHUNK_SIZE)];
			int length = 0;
			while ((length = fileInputStream.read(buffer)) != -1) {
				MD5.update(buffer, 0, length);
			}
			return HexUtils.toHexString(MD5.digest());
		} catch (Exception e) {
			throw new BizException(BizMessage.FILE_MD5_COMPUTE_FAIL);
		}
	}

	/**
	 * 获取mimetype，首选读取二进制流获取mimetype，失败则通过文件名扩展名获取mimetype
	 * @param path
	 * @param filename
	 * @return
	 */
	public static String mimetype(Path path, String filename){
		if (!Files.isRegularFile(path)){
			throw new BizException(BizMessage.PATH_NOT_A_FILE);
		}
		String contentType = Try.of(() -> Magic.getMagicMatch(path.toFile(), false, true).getMimeType()).getOrNull();
		if (Objects.isNull(contentType) || !Character.isLetterOrDigit(contentType.indexOf(0))){
			contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(filename);
		}
		return contentType;
	}

	/**
	 * 获取size
	 */
	public static Long size(Path path) {
		return Try.of(()->Files.size(path)).getOrElseThrow(()->new BizException(BizMessage.FILE_SIZE_GET_FAIL));
	}

	/**
	 * 移动或者重命名
	 */
	public static void move(Path from, Path to){
		if (!from.toFile().renameTo(to.toFile())){
			throw new BizException(BizMessage.FILE_MOVE_FAIL);
		}
		log.info("[{}]重命名为[{}]", from, to);
	}


	/**
	 * 通过文件类型获取与之匹配的后缀名列表，无匹配时返回null
	 * @param fileType FileConst常量有声明, IMAGE_TYPE = 1, DOC_TYPE = 2 ....
	 * @return
	 */
	public static Set<String> getFileExtensionsByType(FileTypeEnum fileType) {

		Set<String> set = new HashSet<>();
		switch (fileType) {
			case IMAGE:
				set = IMG_FILE;
				break;
			case DOC:
				set = DOC_FILE;
				break;
			case VIDEO:
				set = VIDEO_FILE;
				break;
			case MUSIC:
				set = MUSIC_FILE;
				break;
			case APP:
				set = APP_FILE;
				break;
			case OTHER:
				set.addAll(IMG_FILE);
				set.addAll(DOC_FILE);
				set.addAll(VIDEO_FILE);
				set.addAll(MUSIC_FILE);
				set.addAll(APP_FILE);
				break;
			default:
				break;
		}
		return set;
	}
}
