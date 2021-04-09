package io.ddisk.service.impl;

import io.ddisk.dao.RoleRepository;
import io.ddisk.domain.entity.RoleEntity;
import io.ddisk.domain.enums.RoleEnum;
import io.ddisk.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/4
 */
@Service
@Transactional
public class RoleSeviceImpl implements RoleService {

	@Autowired
	private RoleRepository roleRepository;


	/**
	 * 通过RoleEnum查询Role，如不存在该Role则创建
	 *
	 * @param roleEnum
	 * @return
	 */
	@Override
	public RoleEntity findByEnum(RoleEnum roleEnum) {
		return roleRepository.findById(roleEnum.getId()).orElseGet(()->{
			RoleEntity roleEntity = new RoleEntity();
			roleEntity.setId(roleEnum.getId());
			roleEntity.setName(roleEnum.name());
			return roleRepository.save(roleEntity);
		});
	}
}
