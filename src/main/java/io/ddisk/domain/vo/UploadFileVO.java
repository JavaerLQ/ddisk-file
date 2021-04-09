package io.ddisk.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.Collection;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/16
 */
@Data
@Builder
@Schema(name = "文件上传VO",required = true)
public class UploadFileVO {

	@Schema(description = "是否需要合并分片, 也意味着所有切片已经上传成功", example = "true")
	private Boolean needMerge;
	@Schema(description = "跳过上传", example = "true")
	private Boolean skipUpload;
	@Schema(description = "已经上传的分片", example = "[1,2,3]")
	private Collection<Integer> uploaded;
}