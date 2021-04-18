package io.ddisk.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/18
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChunkPathDTO {

	private String fileId;

	private Long chunkSize;
}
