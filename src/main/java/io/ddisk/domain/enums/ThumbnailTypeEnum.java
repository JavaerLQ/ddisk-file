package io.ddisk.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

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
	private Integer width;
	/**
	 * 图片高度
	 */
	private Integer height;
	/**
	 * 缩放比例
	 */
	private Float scale;


	public Boolean isSize(){
		return Arrays.stream(values()).anyMatch(thumbnailTypeEnum -> thumbnailTypeEnum.width!=0F);
	}

	public Boolean isScale(){
		return Arrays.stream(values()).anyMatch(thumbnailTypeEnum -> thumbnailTypeEnum.scale!=0F);
	}

	/**
	 * 获取文件名后面一部分：fileId_?.png
	 * @return
	 */
	public String getSuffixName(){
		String sizeName = String.format("_%dx%d", getWidth(), getHeight());
		String scaleName = String.format("_%f", getScale());
		return String.format("%s%s", isSize()?sizeName:"", isScale()?scaleName:"");
	}
}
