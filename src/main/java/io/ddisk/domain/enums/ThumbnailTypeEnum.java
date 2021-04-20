package io.ddisk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/5
 */
@Getter
@AllArgsConstructor
public enum ThumbnailTypeEnum {

	/**
	 * 生成宽高100像素的图片
	 */
	MIN_SIZE(100,100, 0.F),
	/**
	 * 按比例缩放0.2
	 */
	MIN_SCALE(0, 0, 0.2F)
	;

	/**
	 * 图片宽度
	 */
	private final Integer width;
	/**
	 * 图片高度
	 */
	private final Integer height;
	/**
	 * 缩放比例, 最多两位小数
	 */
	private final Float scale;


	public Boolean isSize(){
		return !(width==0 || height==0);
	}

	public Boolean isScale(){
		return Math.abs(scale) > 0.001;
	}

	/**
	 * 获取文件名后面一部分：fileId_?.png
	 * @return
	 */
	public String getSuffixName(){
		String sizeName = String.format("_%dx%d", getWidth(), getHeight());
		String scaleName = String.format("_%.2f", getScale());
		return String.format("%s%s", isSize()?sizeName:"", isScale()?scaleName:"");
	}
}
