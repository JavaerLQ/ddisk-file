package io.ddisk.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.domain.PageRequest;

/**
 * 前端传来的页码从1开始;
 * 默认显示每页10条数据，起始页码与jpa一致，从零开始。
 * @Author: Richard.Lee
 * @Date: created by 2021/4/2
 */
@Data
@Schema(name = "分页DTO", description = "分页时需要提供的数据")
public class PageDTO {
	@Schema(description = "每页请求条数")
	private Integer pageCount;
	@Schema(description = "当前页数")
	private Integer currentPage;

	public PageRequest buildPageRequest(){
		int size = pageCount<=0 ? 10 : pageCount;
		int page = currentPage<1 ? 0 : currentPage -1;
		return PageRequest.of(page, size);
	}
}
