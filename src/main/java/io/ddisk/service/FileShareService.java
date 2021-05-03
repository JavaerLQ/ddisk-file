package io.ddisk.service;

import io.ddisk.domain.dto.FileShareDTO;
import io.ddisk.domain.entity.FileShareEntity;

import java.util.List;

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
	 */
	void cancelShareFile(String shareId, Long userId);

	/**
	 * 批量取消分享
	 * @param shareIds
	 * @param userId
	 */
	void batchCancelShareFile(List<String> shareIds, Long userId);

	/**
	 * 取消文件分享组
	 * @param shareGroupId
	 * @param userId
	 */
	void cancelShareGroup(String shareGroupId, Long userId);

	/**
	 * 批量取消文件分享组
	 * @param shareGroupIds
	 * @param userId
	 */
	void batchCancelShareGroup(List<String> shareGroupIds, Long userId);

	/**
	 * 保存分享文件
	 * @param shareIds
	 * @param userId
	 */
	void saveShareFile(List<String> shareIds, String pid, Long userId);

	/**
	 * 保存次数+1
	 * @return
	 */
	void sCountPlusOne(FileShareEntity fileShareEntity);

	/**
	 * 下载次数+1
	 * @return
	 */
	void dCountPlusOne(FileShareEntity fileShareEntity);
}
