package io.ddisk.service;

import io.ddisk.domain.dto.FileDTO;
import io.ddisk.domain.dto.FileUploadDTO;
import io.ddisk.domain.dto.MergeFileDTO;
import io.ddisk.domain.entity.FileEntity;
import io.ddisk.domain.enums.RoleEnum;
import io.ddisk.domain.vo.UploadFileVO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/8
 */
public interface FileService {

	/**
	 * 极速上传
	 * @param fileUploadDTO
	 * @return
	 */
	UploadFileVO speedUpload(Long userId, FileUploadDTO fileUploadDTO);

	/**
	 * 文件上传
	 * @param userId
	 * @param fileUploadDTO
	 * @return 返回已上传过的切片
	 */
	Collection<Integer> upload(Long userId, FileUploadDTO fileUploadDTO);

	/**
	 * 合并已上传切片，返回合并后的FileEntity
	 * @param mergeFileDTO
	 */
	FileEntity mergeFile(MergeFileDTO mergeFileDTO);


	/**
	 * 增加引用，实体类状态必须为持久化状态
	 *
	 * @param fileEntity
	 */
	void increaseCount(FileEntity fileEntity);


	/**
	 * 增加引用
	 * @param fileIdentity 文件唯一表示
	 */
	void increaseCount(String fileIdentity);

	/**
	 * 减少引用数，实体类状态必须为持久化状态
	 *
	 * @param fileEntity
	 */
	void decreaseCount(FileEntity fileEntity);


	/**
	 * 减少引用数
	 *
	 * @param fileIdentity
	 */
	void decreaseCount(String fileIdentity);

	/**
	 * 文件下载，管理员可下载任意用户的文件
	 * @param userId
	 * @param role
	 * @param userFileId
	 * @return 返回文件下载信息
	 */
	FileDTO getFileResource(Long userId, RoleEnum role, Long userFileId);

	/**
	 * 获取略缩图
	 * @param userFileId
	 * @return
	 */
	FileDTO getThumbnail(Long userId, Long userFileId);
}
