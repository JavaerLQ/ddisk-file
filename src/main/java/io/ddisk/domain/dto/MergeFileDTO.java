package io.ddisk.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/** 用户文件, 包含文件夹
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
@Data
@Schema(name = "合并文件DTO",required = true)
public class MergeFileDTO {

	@Schema(description = "文件夹ID")
	private Long folderId;

	@NotBlank
	@Schema(description = "文件名")
	private String filename;

	@NotBlank
	@Schema(description = "md5码")
	private String identifier;

	@Schema(description = "切片单位，以1M为一单位: 1048576。最小是1M, 最大50M")
	@Min(1048576)
	@Max(52428800)
	private Long chunkSize;
}
