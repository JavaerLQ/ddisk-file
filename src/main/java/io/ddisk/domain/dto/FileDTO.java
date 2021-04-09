package io.ddisk.domain.dto;

import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/26
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileDTO {

	private String fileName;
	private String extension;
	private String contextType;
	private String url;
	private Long size;

	public FileDTO(String fileName, String contextType, String url) {
		this.fileName = fileName;
		this.contextType = contextType;
		this.url = url;
	}

	public String getFullName(){
		if (StringUtil.isNotBlank(extension)){
			return this.fileName + "." + extension;
		}
		return fileName;
	}
}
