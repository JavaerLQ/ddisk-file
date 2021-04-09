package io.ddisk.dao;

import io.ddisk.domain.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/4
 */
public interface RoleRepository extends JpaRepository<RoleEntity, Long> {
}
