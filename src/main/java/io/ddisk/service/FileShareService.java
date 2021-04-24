package io.ddisk.service;

import io.ddisk.domain.dto.FileShareDTO;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
public interface FileShareService {

	/**
	 * 分享文件
	 */
	void shareFile(FileShareDTO fileShareDTO, Long userId);

	/**
	 * 取消分享文件
	 * @param shareId
	 */
	void cancelShare(String shareId);
}
