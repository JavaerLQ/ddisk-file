package io.ddisk.domain.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/25
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageVO<T>{

	@Schema(description = "总条目数")
	public Long total;

	/**
	 * 需要分页的数据
	 */
	public Collection<T> data;
}
