package io.ddisk.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/24
 */
@Data
@Schema(description = "用户存储状态VO")
@NoArgsConstructor
@AllArgsConstructor
public class UserStorageVO {

	@Schema(description = "最大可用容量")
	private Long maxStorage;

	@Schema(description = "已使用容量")
	private Long usedStorage;


	public void decreaseUsedStorage(Long size){
		this.usedStorage -= size;
	}

	public void increaseUsedStorage(Long size){
		this.usedStorage += size;
	}

	public Boolean isOverflow(){
		return usedStorage > maxStorage;
	}
}
