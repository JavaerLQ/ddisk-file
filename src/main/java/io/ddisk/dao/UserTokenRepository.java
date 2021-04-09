package io.ddisk.dao;

import io.ddisk.domain.entity.UserTokenEntity;
import io.ddisk.domain.enums.TokenTypeEnum;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/7
 */
public interface UserTokenRepository extends JpaRepository<UserTokenEntity, Long> {

	Optional<UserTokenEntity> findByEmailAndType(String email, TokenTypeEnum type);
	Optional<UserTokenEntity> findByEmailAndTypeAndToken(String email, TokenTypeEnum type, String token);
}
