package io.ddisk.domain.consts;

import java.util.Set;

/**
 * 文件的一些常量
 *
 * @Author: Richard.Lee
 * @Date: created by 2021/3/8
 */
public class FileConst {


	/**
	 * 切片大小, 1M
	 */
	public static final Long CHUNK_SIZE = 1024*1024L;

	/**
	 * 根目录ID
	 */
	public static final Long ROOT_ID = null;

	/**
	 * 上传目录
	 */
	public static final String UPLOAD_FOLDER = "upload";

	/**
	 * 切片目录
	 */
	public static final String CHUNK_PATH = "chunk";

	/**
	 * 略缩图路径
	 */
	public static final String THUMBNAIL_PATH = "thumbnail";
	/**
	 * 切片合成文件
	 */
	public static final String FILE_TMP = "file.tmp";

	public static final Long ZERO = 0L;


	/**
	 * 图片文件后缀
	 */
	public static final Set<String> IMG_FILE = Set.of("bmp", "jpg", "png", "tif", "gif", "jpeg");
	/**
	 * 文档文件后缀
	 */
	public static final Set<String> DOC_FILE = Set.of("doc", "docx", "ppt", "pptx", "xls", "xlsx", "txt", "hlp", "wps", "rtf", "pdf", "md");
	/**
	 * 视频文件后缀
	 */
	public static final Set<String> VIDEO_FILE = Set.of("avi", "mp4", "mpg", "mov", "swf", "flv");
	/**
	 * 音乐文件后缀
	 */
	public static final Set<String> MUSIC_FILE = Set.of("wav", "aif", "au", "mp3", "ram", "wma", "mmf", "amr", "aac", "flac");
	/**
	 * 软件包文件后缀
	 */
	public static final Set<String> APP_FILE = Set.of("exe", "app", "deb", "rpm", "ipa", "dmg", "apk");
}
