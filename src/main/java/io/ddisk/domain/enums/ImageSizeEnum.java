package io.ddisk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
@Getter
@AllArgsConstructor
public enum ImageSizeEnum {

	/**
	 * 最小规格图片
	 */
	MIN(100,100);

	/**
	 * 图片宽度
	 */
	private Integer width;
	/**
	 * 图片高度
	 */
	private Integer height;
}
