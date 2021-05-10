package io.ddisk.utils;

import io.ddisk.domain.dto.FileUploadDTO;
import io.ddisk.domain.enums.FileTypeEnum;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.vavr.control.Try;
import jodd.io.FileUtil;
import jodd.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import net.sf.jmimemagic.Magic;
import org.apache.catalina.connector.ClientAbortException;
import org.apache.tomcat.util.buf.HexUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StreamUtils;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
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
	 * 文件上传路径/chunk/文件唯一标识/切片大小/第几切片
	 *
	 * @param fileUploadDTO
	 */
	public static void chunk(FileUploadDTO fileUploadDTO) {

		Try.withResources(
				() -> Optional.ofNullable(fileUploadDTO.getFile())
						.map(file -> Try.of(file::getInputStream).getOrElseThrow(() -> new BizException(BizMessage.UPLOAD_FILE_STREAM_FAIL)))
						.orElseThrow(() -> new BizException(BizMessage.UPLOAD_FILE_NULL)),
				() -> new FileOutputStream(PathUtils.getChunkFilePath(fileUploadDTO.getIdentifier(), fileUploadDTO.getChunkSize(), fileUploadDTO.getChunkNumber()).toFile())
		).of(FileCopyUtils::copy).getOrElseThrow(() -> new BizException(BizMessage.FILE_COPY_FAIL));
	}

	/**
	 * 合并切片
	 *
	 * @param fileId     文件唯一标识
	 * @param chunkTotal 切片数量
	 * @return
	 */
	public static Path mergeFile(String fileId, Long chunkSize, Integer chunkTotal) {

		String cps = PathUtils.getChunkDirPath(fileId, chunkSize).toString();
		Path outPath = Path.of(cps, FILE_TMP);
		if (FileUtil.isExistingFile(outPath.toFile())) {
			return outPath;
		}
		Try.of(() -> Files.createFile(outPath)).getOrElseThrow(() -> new BizException(BizMessage.FILE_CREATE_FILE));
		for (int i = 1; i <= chunkTotal; i++) {
			Path chunkFile = PathUtils.getChunkFilePath(fileId, chunkSize, i);
			byte[] bytes = Try.of(() -> Files.readAllBytes(chunkFile)).getOrElseThrow(() -> {
				deleteRecursively(outPath);
				return new BizException(BizMessage.UPLOAD_FILE_GET_BYTES_FAIL);
			});
			Try.run(() -> Files.write(outPath, bytes, StandardOpenOption.APPEND));
		}
		log.info("合并{}个切片文件[{}]", chunkTotal, fileId);
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
	 *
	 * @return md5 value
	 */
	public static String md5(Path filePath) {

		try (FileInputStream fileInputStream = new FileInputStream(filePath.toFile())) {
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
	 *
	 * @param path
	 * @param filename
	 * @return
	 */
	public static String mimetype(Path path, String filename) {
		if (!Files.isRegularFile(path)) {
			throw new BizException(BizMessage.PATH_NOT_A_FILE);
		}
		String contentType = Try.of(() -> Magic.getMagicMatch(path.toFile(), false, true).getMimeType()).getOrNull();
		if (Objects.isNull(contentType) || !Character.isLetterOrDigit(contentType.indexOf(0))) {
			contentType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(filename);
		}
		return contentType;
	}

	/**
	 * 获取size
	 */
	public static Long size(Path path) {
		return Try.of(() -> Files.size(path)).getOrElseThrow(() -> new BizException(BizMessage.FILE_SIZE_GET_FAIL));
	}

	/**
	 * 获取文件全名
	 */
	public static String getFullName(String name, String extension) {
		StringBuilder sb = new StringBuilder();
		sb.append(name);
		if (StringUtil.isNotBlank(extension)) {
			sb.append(extension);
		}
		return sb.toString();
	}

	/**
	 * 移动或者重命名
	 */
	public static void move(Path from, Path to) {
		if (!from.toFile().renameTo(to.toFile())) {
			throw new BizException(BizMessage.FILE_MOVE_FAIL);
		}
		log.info("[{}]重命名为[{}]", from, to);
	}


	/**
	 * 通过文件类型获取与之匹配的后缀名列表，无匹配时返回null
	 *
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

	/**
	 * 文件支持分块下载和断点续传
	 */
	public static void chunkDownload(String filename, String fileUrl, Long size, String contentType) {

		String username = Optional.ofNullable(SpringWebUtils.getRequestUser()).map(LoginUser::getUsername).orElse("匿名用户");
		HttpServletRequest request = SpringWebUtils.getRequest();
		HttpServletResponse response = SpringWebUtils.getResponse();
		String range = request.getHeader("Range");
		//开始下载位置
		long startByte = 0;
		//结束下载位置
		long endByte = size - 1;
		log.debug("文件[{}]开始位置：{}，文件结束位置：{}，文件总长度：{}", filename, startByte, endByte, size);

		//有range的话
		if (range != null && range.contains("bytes=") && range.contains("-")) {
			range = range.substring(range.lastIndexOf("=") + 1).trim();
			String[] ranges = range.split("-");
			try {
				//判断range的类型
				if (ranges.length == 1) {
					//类型一：bytes=-2343
					if (range.startsWith("-")) {
						endByte = Long.parseLong(ranges[0]);
					}
					//类型二：bytes=2343-
					else if (range.endsWith("-")) {
						startByte = Long.parseLong(ranges[0]);
					}
				}
				//类型三：bytes=22-2343
				else if (ranges.length == 2) {
					startByte = Long.parseLong(ranges[0]);
					endByte = Long.parseLong(ranges[1]);
				}
				//http状态码要为206：表示获取部分内容
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
			} catch (NumberFormatException e) {
				startByte = 0;
				endByte = size - 1;
				log.error("Range Occur Error,Message:{}", e.getLocalizedMessage());
			}
		}

		//要下载的长度
		long contentLength = endByte - startByte + 1;

		//各种响应头设置
		//支持断点续传，获取部分字节内容：
		response.setHeader("Accept-Ranges", "bytes");
		response.setContentType(contentType);
		//inline表示浏览器直接使用，attachment表示下载，fileName表示下载的文件名, 解决下载文件时文件名乱码问题
		String contentDisposition = String.format("inline;filename=%s", new String(filename.getBytes(StandardCharsets.UTF_8), StandardCharsets.ISO_8859_1));
		response.setHeader("Content-Disposition", contentDisposition);
		response.setContentLengthLong(contentLength);
		// Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
		response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + size);

		try (
				InputStream inputStream = new FileInputStream(fileUrl);
				BufferedOutputStream outputStream = new BufferedOutputStream(response.getOutputStream());
		) {
			StreamUtils.copyRange(inputStream, outputStream, startByte, endByte);
			log.info(String.format("用户[%s]下载[ %s ]完毕：%d-%d", username, filename, startByte, endByte));
		} catch (ClientAbortException e) {
			log.warn(String.format("用户[%s]停止[ %s ]下载：%d-%d", username, filename, startByte, endByte));
			//捕获此异常表示拥护停止下载
		} catch (IOException e) {
			log.info(String.format("用户[%s]下载[ %s ]IO异常，Message：{}", filename, e.getLocalizedMessage()));
		}
	}
}
