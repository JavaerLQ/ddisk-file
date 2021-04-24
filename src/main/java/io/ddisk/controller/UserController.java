package io.ddisk.controller;

import io.ddisk.domain.dto.RegisterDTO;
import io.ddisk.domain.vo.LoginUser;
import io.ddisk.domain.vo.UserStorageVO;
import io.ddisk.service.UserService;
import io.ddisk.service.UserStorageService;
import io.ddisk.utils.SpringWebUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * 用户的登录，注销交由spring security处理
 *
 * @Author: Richard.Lee
 * @Date: created by 2021/2/20
 */
@Tag(name = "User", description = "该接口为用户接口，主要做用户登录，注册和校验token")
@RequestMapping("user")
@RestController
public class UserController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserStorageService userStorageService;

	@Operation(summary = "用户注册, 步骤一", description = "获取注册令牌，令牌发送至用户邮箱", tags = {"user"})
	@GetMapping(value = "register")
	@Parameters({
			@Parameter(name = "email", description = "用户邮箱", required = true)
	})
	public ResponseEntity<Void> sendRegisterToken(@NotBlank @Email String email) {

		userService.sendEmailToRegister(email);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "用户注册, 步骤二", description = "注册账号", tags = {"user"})
	@PostMapping(value = "register")
	public ResponseEntity<Void> registerUser(@Validated @RequestBody RegisterDTO registerDTO) {

		userService.addUser(registerDTO);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "设置头像", description = "设置用户头像", tags = {"user"})
	@PostMapping(value = "avatar")
	@Parameters({
			@Parameter(name = "fileId", description = "用户文件id", required = true)
	})
	public ResponseEntity<Void> setAvator(String fileId) {

		LoginUser user = SpringWebUtils.requireLogin();
		userService.setAvator(user.getId(), fileId);
		user.setImgUrl(String.valueOf(fileId));
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "获取用户信息", description = "获取用户信息", tags = {"user"})
	@GetMapping(value = "info")
	public ResponseEntity<LoginUser> getLoginUser() {

		return ResponseEntity.ok().body(SpringWebUtils.requireLogin());
	}

	@Operation(summary = "校验用户名", description = "检查用户是否已被注册", tags = {"user"})
	@GetMapping(value = "exist/username")
	@Parameters({
			@Parameter(name = "username", description = "用户名", required = true)
	})
	public ResponseEntity<Void> existUsername(
		@Length(min = 4, max = 16, message = "用户名长度必须在7-20位") String username) {

		userService.notExistUsername(username);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "校验邮箱", description = "检查邮箱是否已被使用", tags = {"user"})
	@GetMapping(value = "exist/email")
	@Parameters({
			@Parameter(name = "email", description = "用户邮箱", required = true)
	})
	public ResponseEntity<LoginUser> existEmail(@Email @NotBlank String email) {

		userService.notExistUsername(email);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "忘记密码，步骤1", description = "忘记密码之发送密码重置邮件", tags = {"user"})
	@GetMapping(value = "reset/passwd")
	@Parameters({
			@Parameter(name = "email", description = "用户邮箱", required = true)
	})
	public ResponseEntity<Void> forgetPasswd(@Email @NotBlank String email) {

		userService.sendEmailToResetPassword(email);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "忘记密码，步骤2", description = "忘记密码之重置密码", tags = {"user"})
	@PostMapping(value = "reset/passwd")
	@Parameters({
			@Parameter(name = "email", description = "用户邮箱", required = true),
			@Parameter(name = "password", description = "新密码", required = true),
			@Parameter(name = "token", description = "令牌，从邮件中获取", required = true)
	})
	public ResponseEntity<Void> regeneratePasswd(
			@Email @NotBlank String email,
			@Length(min = 7, max = 20, message = "密码长度必须在7-20位") String password,
			@Length(min = 36, max = 36, message = "密码长度必须在7-20位") String token) {

		userService.forgetPassword(email, password, token);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "修改密码", description = "修改密码", tags = {"user"})
	@Parameters({
			@Parameter(name = "oldPassword", description = "原密码", required = true),
			@Parameter(name = "newPassword", description = "新密码", required = true)
	})
	@GetMapping(value = "passwd")
	public ResponseEntity<Void> passwd(
			@Length(min = 7, max = 20, message = "密码长度必须在7-20位") String oldPassword,
			@Length(min = 7, max = 20, message = "密码长度必须在7-20位") String newPassword
	) {
		LoginUser user = SpringWebUtils.requireLogin();
		userService.passwd(user.getId(), oldPassword, newPassword);
		return ResponseEntity.ok().build();
	}

	@Operation(summary = "用户内存使用", description = "获取用户磁盘信息，已使用和未使用大小", tags = {"user"})
	@GetMapping(value = "storage/info")
	public ResponseEntity<UserStorageVO> getStorageInfo() {

		UserStorageVO vo = userStorageService.getStorageInfo(SpringWebUtils.requireLogin());
		return ResponseEntity.ok().body(vo);
	}

}
