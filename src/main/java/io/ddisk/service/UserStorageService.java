package io.ddisk.service;

import io.ddisk.domain.entity.UserFileEntity;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.domain.vo.UserStorageVO;

import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/24
 */
public interface UserStorageService {


	/**
	 * 用户必须已登录，用户能否装下size大小的文件，不能则抛出异常
	 * @param size
	 */
	void hasSize(Long size);
	/**
	 * 用户必须已登录
	 * 获取用户的存储空间使用信息
	 */
	UserStorageVO getStorageInfo();

	/**
	 * 获取用户的存储空间使用信息
	 * @param user
	 * @return
	 */
	UserStorageVO getStorageInfo(LoginUser user);


	/**
	 * 用户必须已登录
	 * 计算用户已使用的空间大小
	 * @return
	 */
	Long computeUsedStorage();
	/**
	 * 计算用户已使用的空间大小
	 * @param userId
	 * @return
	 */
	Long computeUsedStorage(Long userId);


	/**
	 * 用户必须已登录
	 * 增加用户已使用空间
	 * @param size 增加大小
	 */
	UserStorageVO increaseStorage(Long size);

	/**
	 * 增加用户已使用空间
	 * @param user
	 * @param size 增加大小
	 */
	UserStorageVO increaseStorage(LoginUser user, Long size);


	/**
	 * 用户必须已登录
	 * 减少用户已使用空间
	 * @param size
	 * @return
	 */
	UserStorageVO decreaseStorage(Long size);
	/**
	 * 减少用户已使用空间
	 * @param user
	 * @param size
	 * @return
	 */
	UserStorageVO decreaseStorage(LoginUser user, Long size);

	/**
	 * 用户必须已登录
	 * 删除文件计算内存
	 */
	void deleteFileCalculator(List<UserFileEntity> userFileEntityList, boolean isDelete);
}
