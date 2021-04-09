package io.ddisk.domain.vo.base;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/10
 */
@Data
@Builder
@Schema(name = "错误信息VO", description = "异常返回数据")
public class ErrorVO {
	@Schema(description = "状态码")
	private Integer status;
	@Schema(description = "错误原因")
	private String msg;
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	@Schema(description = "数据")
	private Object data;
}
