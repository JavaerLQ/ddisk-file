package io.ddisk.domain.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/8
 */
@Getter
@Schema(name = "文件类型", description = "用于查询某一类型文件，例如视频文件，音乐文件")
@AllArgsConstructor
public enum FileTypeEnum {
	/**
	 * 不区分文件类型，获取所有文件
	 */
	@Schema(description = "不区分文件类型")
	ALL,
	/**
	 * 图片文件类型
	 */
	@Schema(description = "音乐文件")
	IMAGE,
	/**
	 * 文档文件类型
	 */
	@Schema(description = "文档文件")
	DOC,
	/**
	 * 视频文件类型
	 */
	@Schema(description = "视频文件")
	VIDEO,
	/**
	 * 音乐文件类型
	 */
	@Schema(description = "音乐文件")
	MUSIC,
	/**
	 * 软件包文件类型
	 */
	@Schema(description = "软件包文件")
	APP,
	/**
	 * 其他文件类型
	 */
	@Schema(description = "其他文件")
	OTHER,
}
