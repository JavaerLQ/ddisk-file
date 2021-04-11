package io.ddisk.domain.dto;

import jodd.net.MimeTypes;
import jodd.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.MimeTypeUtils;

import javax.activation.MimetypesFileTypeMap;
import java.util.Objects;

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

	public FileDTO(String fileName, Long size, String url) {
		this.fileName = fileName;
		this.size = size;
		this.url = url;
	}

	public String getContextType() {
		if (Objects.nonNull(contextType)){
			return contextType;
		}
		contextType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(getFullName());
		if (MimeTypes.MIME_APPLICATION_OCTET_STREAM.equals(contextType)){
			contextType = MimetypesFileTypeMap.getDefaultFileTypeMap().getContentType(url);
		}
		return contextType;
	}

	public String getFullName(){
		if (StringUtil.isNotBlank(extension)){
			return this.fileName + "." + extension;
		}
		return fileName;
	}
}
