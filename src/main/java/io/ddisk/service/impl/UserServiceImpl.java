package io.ddisk.service.impl;

import io.ddisk.dao.UserRepository;
import io.ddisk.dao.UserStorageRepository;
import io.ddisk.domain.consts.EmailConst;
import io.ddisk.domain.dto.RegisterDTO;
import io.ddisk.domain.entity.RoleEntity;
import io.ddisk.domain.entity.UserEntity;
import io.ddisk.domain.entity.UserStorageEntity;
import io.ddisk.domain.entity.UserTokenEntity;
import io.ddisk.domain.enums.RoleEnum;
import io.ddisk.domain.enums.TokenTypeEnum;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.service.*;
import io.vavr.control.Try;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
@Slf4j
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserStorageRepository userStorageRepository;
	@Autowired
	private UserTokenService userTokenService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserFileService userFileService;


	@Override
	public void addUser(RegisterDTO registerDTO) {

		// 验证令牌
		userTokenService.useUserToken(registerDTO.getEmail(), registerDTO.getToken(), TokenTypeEnum.REGISTER);

		// 用户名已注册
		if (userRepository.findByUsername(registerDTO.getUsername()).isPresent()) {
			throw new BizException(BizMessage.USERNAME_EXIST);
		}

		// 邮箱已绑定
		if (userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
			throw new BizException(BizMessage.EMAIL_EXIST);
		}

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		String encodePassword = encoder.encode(registerDTO.getPassword());

		UserEntity userEntity = new UserEntity();
		BeanUtils.copyProperties(registerDTO, userEntity);

		RoleEntity role = roleService.findByEnum(RoleEnum.TRIAL);
		userEntity.setPassword(encodePassword);
		userEntity.setRegisterTime(new Date());
		userEntity.setRole(role);
		userEntity.setAccountNonLocked(true);

		userEntity = userRepository.save(userEntity);

		UserStorageEntity storageEntity = new UserStorageEntity();
		storageEntity.setUsedStorage(0L);
		storageEntity.setUserId(userEntity.getId());
		userStorageRepository.save(storageEntity);

		log.info("用户注册成功: 用户名{}, 邮箱{}, 密码{}", registerDTO.getUsername(), registerDTO.getEmail(), registerDTO.getPassword());
	}

	/**
	 * 用户登录, 通过用户名和密码查询唯一一个用户
	 * @param username
	 * @param password
	 * @return
	 */
	@Override
	public UserEntity userLogin(String username,  String password) {
		return userRepository.findByUsernameAndPassword(username, password).orElseThrow(()->new BizException(BizMessage.BAD_CREDENTIALS));
	}

	/**
	 * 校验用户名是否存在
	 * 用户名存在则抛异常
	 *
	 * @param username
	 */
	@Override
	public void notExistUsername(String username) {
		UserEntity user = new UserEntity();
		user.setUsername(username);
		if(userRepository.exists(Example.of(user))){
			throw new BizException(BizMessage.USERNAME_EXIST);
		}
	}

	/**
	 * 校验邮箱是否存在
	 * 存在则抛异常
	 *
	 * @param email
	 */
	@Override
	public void notExistEmail(String email) {

		UserEntity user = new UserEntity();
		user.setEmail(email);
		if(userRepository.exists(Example.of(user))){
			throw new BizException(BizMessage.EMAIL_EXIST);
		}
	}

	/**
	 * 发送邮件重置用户密码
	 *
	 * @param email
	 */
	@Override
	public void sendEmailToResetPassword(String email) {

		// 有效期为5分钟
		Date expiresTime = Date.from(LocalDateTime.now().plusMinutes(5L).atZone(ZoneId.systemDefault()).toInstant());
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new BizException(BizMessage.EMAIL_NOT_EXIST));
		UserTokenEntity userTokenEntity = userTokenService.nonUserToken(user.getEmail(), TokenTypeEnum.FORGET);
		// 生成令牌
		String token = UUID.randomUUID().toString();
		userTokenService.addToken(userTokenEntity, token, expiresTime);

		log.info("用户{}忘记密码，正在尝试更改密码", user.getUsername());
		String content = String.format(EmailConst.RESET_PASSWD_HTML_CONTENT, user.getUsername(), 5, token);
		Try.run(()->emailService.sendHtmlMail(email, EmailConst.RESET_PASSWD_SUBJECT, content));
	}

	/**
	 * 忘记密码通过邮件重置密码
	 * @param password
	 * @param token
	 */
	@Override
	public void forgetPassword(String email, String password, String token){

		// 消费令牌
		userTokenService.useUserToken(email, token, TokenTypeEnum.FORGET);
		UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() -> new BizException(BizMessage.USER_TOKEN_INVALID));
		userEntity.setPassword(new BCryptPasswordEncoder().encode(password));
		userRepository.save(userEntity);

		log.info("用户{}忘记密码, 新密码[{}]", userEntity.getUsername(), password);
		// 发送邮件，通知密码修改成功
		String content = String.format(EmailConst.RESET_PASSWD_SUCCESS_HTML_CONTENT, userEntity.getUsername(), password);
		Try.run(()->emailService.sendHtmlMail(userEntity.getEmail(), EmailConst.RESET_PASSWD_SUCCESS_SUBJECT, content));
	}

	/**
	 * 修改用户密码，修改成功发送邮件通知, 不抛出异常则代表成功
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 */
	@Override
	public void passwd(Long userId, String oldPassword, String newPassword) {

		UserEntity user = userRepository.findById(userId).orElseThrow(() -> new BizException(BizMessage.USER_NOT_EXIST));

		PasswordEncoder encoder = new BCryptPasswordEncoder();

		if(!encoder.matches(user.getPassword(), oldPassword)){
			throw new BizException(BizMessage.PASSWORD_NO_MATCH);
		}
		user.setPassword(encoder.encode(newPassword));
		userRepository.save(user);

		log.info("用户{}更改密码, [{}]->[{}]", user.getUsername(), oldPassword, newPassword);
		// 发送邮件，通知密码修改成功
		String content = String.format(EmailConst.RESET_PASSWD_SUCCESS_HTML_CONTENT, user.getUsername(), newPassword);
		Try.run(()->emailService.sendHtmlMail(user.getEmail(), EmailConst.RESET_PASSWD_SUCCESS_SUBJECT, content));
	}

	/**
	 * 设置头像
	 *
	 * @param userId
	 * @param fileId
	 */
	@Override
	public void setAvator(Long userId, Long fileId) {
		userFileService.checkImage(userId, fileId);
		UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new BizException(BizMessage.USER_NOT_EXIST));
		userEntity.setImgUrl(fileId);
		userRepository.save(userEntity);
		log.info("用户{}更换头像", userEntity.getUsername());
	}

	/**
	 * 用户注册，发送令牌至用户邮箱
	 *
	 * @param email
	 */
	@Override
	public void sendEmailToRegister(String email) {
		// 有效期为5分钟
		Date expiresTime = Date.from(LocalDateTime.now().plusMinutes(5L).atZone(ZoneId.systemDefault()).toInstant());
		notExistEmail(email);
		UserTokenEntity userTokenEntity = userTokenService.nonUserToken(email, TokenTypeEnum.REGISTER);
		// 生成令牌, 6位数
		String token = UUID.randomUUID().toString().substring(0, 6);
		userTokenService.addToken(userTokenEntity, token, expiresTime);

		String content = String.format(EmailConst.USER_REGISTER_HTML_CONTENT, 5, token);
		Try.run(()->emailService.sendHtmlMail(email, EmailConst.USER_REGISTER_SUBJECT, content));
	}
}
