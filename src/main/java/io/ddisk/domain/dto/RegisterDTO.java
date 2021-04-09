package io.ddisk.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
@Data
@Schema(name = "注册用户DTO", description = "用户注册时需要提交的表单数据", required = true)
public class RegisterDTO {

	@Length(min = 4, max = 16, message = "用户名长度必须在4-16位")
	@Schema(description = "用户名", required = true)
	private String username;
	@Email
	@NotBlank
	@Schema(description = "邮箱", required = true)
	private String email;
	@Length(min = 7, max = 20, message = "密码长度必须在7-20位")
	@Schema(description = "密码", required = true)
	private String password;
	@Length(min = 6, max = 6, message = "令牌长度6位")
	@Schema(description = "令牌", required = true)
	private String token;
}
