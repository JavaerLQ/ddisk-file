package io.ddisk.domain.dto;

import io.ddisk.domain.entity.FileShareGroupEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/21
 */
@Data
@Schema(name = "文件分享DTO", required = true)
public class FileShareDTO {

	/**
	 * {@link io.ddisk.domain.entity.UserFileEntity} 的 ID
	 */
	@Schema(description = "用户文件Id，可以是文件也可以是文件夹", required = true)
	@NotEmpty
	private List<String> userFileIds;
	@Schema(description = "截至时间，不传为不限时间", nullable = true, required = false)
	private Date dueDate;
	@Schema(description = "6位分享密钥", nullable = true, required = false)
	@Length(min = 6, max = 6)
	private String key;
	@Schema(description = "允许匿名下载", nullable = false, required = true)
	private Boolean anonymousDownload;
	@Schema(description = "分享大厅可见", nullable = false, required = true)
	private Boolean visible;


	public FileShareGroupEntity createFileShareGroupEntity(){
		FileShareGroupEntity groupEntity = new FileShareGroupEntity();
		BeanUtils.copyProperties(this, groupEntity);
		groupEntity.setCreateTime(new Date());
		groupEntity.setActive(true);
		return groupEntity;
	}


}
