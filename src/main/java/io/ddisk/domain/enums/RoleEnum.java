package io.ddisk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.stream.Stream;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/4
 */
@Getter
@AllArgsConstructor
public enum RoleEnum {

	/**
	 * 管理员用户, 1T容量
	 */
	ADMIN(1L, "管理员", 1024*1024*1024*1024L),
	/**
	 * 普通用户，默认50G
	 */
	NORMAL(2L, "普通用户", 1024*1024*1024*50L),

	/**
	 * 试用用户，默认100M
	 */
	TRIAL(3L, "试用用户", 1024*1024*100L);

	/**
	 * 角色ID
	 */
	private final Long id;
	/**
	 * 角色描述
	 */
	private final String description;

	/**
	 * 拥有最大容量
	 */
	private final Long maxStorageSize;

	/**
	 * 通过角色名获取角色枚举类型
	 * @param name
	 * @return
	 */
	public static RoleEnum getByName(String name) {
		return Stream.of(values())
				.filter(roleEnum -> roleEnum.name().equalsIgnoreCase(name))
				.findAny()
				.orElse(null);
	}


	/**
	 * 通过角色ID获取角色枚举类型
	 * @param id
	 * @return
	 */
	public static RoleEnum getById(Long id) {
		return Stream.of(values())
				.filter(roleEnum -> roleEnum.id.equals(id))
				.findAny()
				.orElse(null);
	}

	/**
	 * 通过角色描述获取角色枚举类型
	 * @param description
	 * @return
	 */
	public static RoleEnum getByDescription(String description) {
		return Stream.of(values())
				.filter(roleEnum -> roleEnum.description.equalsIgnoreCase(description))
				.findAny()
				.orElse(null);
	}
}
