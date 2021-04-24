package io.ddisk.domain.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.ddisk.domain.entity.UserFileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jodd.util.StringPool;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/1
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(name = "目录树")
public class DirTreeNode {


	@Schema(description = "用户文件id")
	private String id;

	@JsonProperty("label")
	@Schema(description ="用户文件名")
	private String filename;

	@Schema(description = "所在目录，根目录为null")
	private String pid;

	@Schema(description = "深度")
	private Long depth;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Schema(description = "子目录")
	private List<DirTreeNode> children;


	public static DirTreeNode root(){
		return new DirTreeNode(null, StringPool.SLASH, null, 0L, null);
	}

	public static DirTreeNode create(UserFileEntity userFileEntity, Long depth, List<DirTreeNode> children){
		return new DirTreeNode(userFileEntity.getId(), userFileEntity.getFilename(), userFileEntity.getPid(), depth, children);
	}
}
