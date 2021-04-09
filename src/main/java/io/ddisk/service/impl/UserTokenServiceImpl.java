package io.ddisk.service.impl;

import io.ddisk.dao.UserTokenRepository;
import io.ddisk.domain.entity.UserTokenEntity;
import io.ddisk.domain.enums.TokenTypeEnum;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.service.UserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/7
 */
@Service
@Transactional(rollbackFor = Throwable.class)
public class UserTokenServiceImpl implements UserTokenService {
	@Autowired
	private UserTokenRepository userTokenRepository;

	/**
	 * 获取一个无效的token，生效中则抛出异常
	 * @param email
	 * @param type
	 * @return
	 */
	@Override
	public UserTokenEntity nonUserToken(String email, TokenTypeEnum type) {
		return userTokenRepository.findByEmailAndType(email, type)
				.map(ut->{
					Date today = new Date();
					if (ut.getActive()){
						// 已经过期
						if (ut.getExpiresTime().before(today)){
							ut.setActive(false);
							return userTokenRepository.save(ut);
						}
						throw new BizException(BizMessage.USER_TOKEN_VALID);
					}
					return ut;
				})
				.orElse(new UserTokenEntity(email, type));
	}

	/**
	 * 获取一个有用的令牌
	 *
	 * @param token
	 * @param type
	 */
	@Override
	public UserTokenEntity haveUserToken(String email, String token, TokenTypeEnum type) {
		return userTokenRepository.findByEmailAndTypeAndToken(email, type, token).map(
				userTokenEntity -> {
					Date today = new Date();
					if (!userTokenEntity.getActive() || userTokenEntity.getExpiresTime().before(today)){
						throw new BizException(BizMessage.USER_TOKEN_INVALID);
					}
					return userTokenEntity;
				}
		).orElseThrow(()->new BizException(BizMessage.USER_TOKEN_INVALID));
	}


	/**
	 * 添加令牌（提供一个附带userid,type的UserTokenEntity
	 *
	 * @param token
	 * @param expiresTime
	 */
	@Override
	public void addToken(UserTokenEntity userToken, String token, Date expiresTime) {
		userToken.setToken(token);
		userToken.setExpiresTime(expiresTime);
		userToken.setActive(true);
		userTokenRepository.saveAndFlush(userToken);
	}

	/**
	 * 使用token，验证token有效性，使之失效。无效token抛出异常
	 *
	 * @param email
	 * @param token
	 * @param type
	 */
	@Override
	public void useUserToken(String email, String token, TokenTypeEnum type) {
		UserTokenEntity userTokenEntity = haveUserToken(email, token, type);
		userTokenEntity.setActive(false);
		userTokenRepository.save(userTokenEntity);
	}
}
