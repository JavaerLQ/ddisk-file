package io.ddisk.dao;

import io.ddisk.domain.entity.UserStorageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/22
 */
public interface UserStorageRepository extends JpaRepository<UserStorageEntity, Long> {

	Optional<UserStorageEntity> findByUserId(Long userId);

	List<UserStorageEntity> findAllByUserIdIn(Collection<Long> userIds);
}
