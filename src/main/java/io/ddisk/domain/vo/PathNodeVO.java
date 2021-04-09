package io.ddisk.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文件路径树
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PathNodeVO {
	/**
	 * 用户文件Id
	 */
	private Long id;
	/**
	 * 父id
	 */
	private Long pid;
	/**
	 * 用户文件名
	 */
	private String name;

	public PathNodeVO self(){
		return this;
	}
}
