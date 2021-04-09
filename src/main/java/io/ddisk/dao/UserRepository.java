package io.ddisk.dao;

import io.ddisk.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {

	/**
	 * 通过用户名查找用户
	 * @param username
	 * @param email
	 * @return
	 */
	Optional<UserEntity> findByUsername(String username);

	/**
	 * 通过邮箱查找用户
	 * @param username
	 * @param email
	 * @return
	 */
	Optional<UserEntity> findByEmail(String email);

	/**
	 * 通过用户名和密码查找用户
	 */
	Optional<UserEntity> findByUsernameAndPassword(String username, String password);
}
