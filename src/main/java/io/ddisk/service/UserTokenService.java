package io.ddisk.service;

import io.ddisk.domain.entity.UserTokenEntity;
import io.ddisk.domain.enums.TokenTypeEnum;

import java.util.Date;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/7
 */
public interface UserTokenService {

	/**
	 * 获取一个无效的token，生效中则抛出异常
	 * @param email
	 * @param type
	 * @return
	 */
	UserTokenEntity nonUserToken(String email, TokenTypeEnum type);

	/**
	 * 获取一个有效的令牌，正在生效的令牌
	 */
	UserTokenEntity haveUserToken(String email, String token, TokenTypeEnum type);

	/**
	 * 添加令牌（提供一个附带email,type的UserTokenEntity
	 */
	void addToken(UserTokenEntity userToken, String token, Date expiresTime);

	/**
	 * 使用token，验证token有效性，使之失效。无效token抛出异常
	 * @param email
	 * @param token
	 * @param type
	 */
	void useUserToken(String email, String token, TokenTypeEnum type);
}
