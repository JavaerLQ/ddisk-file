package io.ddisk.service;

import io.ddisk.domain.dto.RegisterDTO;
import io.ddisk.domain.entity.UserEntity;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
public interface UserService {
	void addUser(RegisterDTO registerDTO);

	/**
	 * 用户登录, 通过用户名和密码查询唯一一个用户
	 * @param username
	 * @param password
	 * @return
	 */
	UserEntity userLogin(String username, String password);

	/**
	 * 校验用户名是否存在
	 * 用户名存在则抛异常
	 * @param username
	 */
	void notExistUsername(String username);

	/**
	 * 校验邮箱是否存在
	 * 存在则抛异常
	 * @param email
	 */
	void notExistEmail(String email);

	/**
	 * 发送邮件重置用户密码
	 * @param email
	 */
	void sendEmailToResetPassword(String email);

	/**
	 * 忘记密码通过邮件重置密码，从token令牌中解析用户信息。
	 * @param password
	 * @param token
	 */
	void forgetPassword(String email, String password, String token);

	/**
	 * 修改用户密码，修改成功发送邮件通知
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 */
	void passwd(Long userId, String oldPassword, String newPassword);

	/**
	 * 设置头像
	 * @param userId
	 * @param fileId
	 */
	void setAvator(Long userId, Long fileId);

	/**
	 * 用户注册，发送令牌至用户邮箱
	 * @param email
	 */
	void sendEmailToRegister(String email);
}
