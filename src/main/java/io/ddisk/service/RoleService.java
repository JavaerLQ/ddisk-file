package io.ddisk.service;

import io.ddisk.domain.entity.RoleEntity;
import io.ddisk.domain.enums.RoleEnum;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/4
 */
public interface RoleService {
	/**
	 * 通过RoleEnum查询Role，如不存在该Role则创建
	 * @param roleEnum
	 * @return
	 */
	RoleEntity findByEnum(RoleEnum roleEnum);
}
