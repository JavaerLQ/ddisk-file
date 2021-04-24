package io.ddisk.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.ddisk.domain.entity.UserFileEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/1
 */
@Data
@Schema(name = "文件基本信息描述", description = "某目录下的所有文件")
public class FileVO {


	@Schema(description = "用户文件id")
	private String id;

	@Schema(description ="用户文件名")
	private String filename;

	@Schema(description = "扩展名")
	private String extension;

	@Schema(description = "所在目录，根目录为null")
	private String pid;

	@Schema(description = "文件大小")
	private Long fileSize;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "文件最后修改时间")
	private Date updateTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "文件创建时间")
	private Date createTime;

	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
	@Schema(description = "文件删除时间")
	private Date deleteTime;

	@Schema(description = "文件是否是目录，用于区分文件夹与普通文件")
	private Boolean dir;

	public static FileVO create(UserFileEntity userFileEntity, Long fileSize){
		FileVO fileVO = new FileVO();
		BeanUtils.copyProperties(userFileEntity, fileVO, FileVO.class);
		fileVO.setFileSize(fileSize);
		return fileVO;
	}
}
