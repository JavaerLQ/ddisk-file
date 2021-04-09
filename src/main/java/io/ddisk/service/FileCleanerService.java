package io.ddisk.service;

/**
 * 文件清理器service
 * @Author: Richard.Lee
 * @Date: created by 2021/4/6
 */
public interface FileCleanerService {

	/**
	 * 清理已合并文件
	 */
	void cleanMergedFiles();

	/**
	 * 清理未完成的切片
	 */
	void cleanIncompleteChunks();

	/**
	 * 清理count为0的文件
	 */
	void cleanCount0Files();

	/**
	 * 清理缩略图垃圾文件
	 */
	void cleanThumbnail();

	/**
	 * 清理回收站文件
	 */
	void cleanRecycleFile();
}
