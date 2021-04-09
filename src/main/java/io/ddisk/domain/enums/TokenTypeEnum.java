package io.ddisk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 令牌类型
 * @Author: Richard.Lee
 * @Date: created by 2021/4/7
 */
@Getter
@AllArgsConstructor
public enum TokenTypeEnum {

	/**
	 * 忘记密码验证令牌类型
	 */
	FORGET(1,"忘记密码-验证令牌-类型"),

	/**
	 * 用户注册验证码
	 */
	REGISTER(2,"用户注册-验证令牌-类型");

	private Integer type;
	private String description;
}
