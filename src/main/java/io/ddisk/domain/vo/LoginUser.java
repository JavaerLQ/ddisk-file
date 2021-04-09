package io.ddisk.domain.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.ddisk.domain.entity.UserEntity;
import io.ddisk.domain.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jodd.bean.BeanCopy;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/5
 */
@Data
@Schema(name = "用户信息", description = "用户登录成功返回数据")
public class LoginUser implements UserDetails {

	public LoginUser(UserEntity userEntity){
		BeanCopy.from(userEntity).to(this).copy();
		this.role = RoleEnum.getByName(userEntity.getRole().getName());
		this.accountNonLocked = userEntity.getAccountNonLocked();
	}
	/**
	 * 用户id
	 */
	@Schema(description = "用户ID", example = "1")
	private Long id;

	/**
	 * 用户名
	 */
	@Schema(description = "用户名", example = "richard")
	private String username;

	/**
	 * 密码
	 */
	@Schema(description = "登录密码", example = "password1234")
	private String password;

	/**
	 * 用户邮箱
	 */
	@Schema(description = "邮箱", example = "974102233@qq.com")
	private String email;

	/**
	 * 用户头像链接，指向File的ID
	 */
	@Schema(description = "头像url", example = "http://xxx.apple.png")
	private String imgUrl;

	/**
	 * 注册时间
	 */
	@Schema(description = "用户注册时间，时间戳", example = "1614873600000")
	private Date registerTime;

	/**
	 * 用户是否被锁定
	 */
	@Schema(description = "用户账号是否被锁定", example = "true")
	private Boolean accountNonLocked;

	/**
	 * 用户角色
	 */
	@Schema(description = "用户角色", example = "admin")
	private RoleEnum role;


	/**
	 * Returns the authorities granted to the user. Cannot return <code>null</code>.
	 *
	 * @return the authorities, sorted by natural key (never <code>null</code>)
	 */
	@Override
	@JsonIgnore
	public Collection<? extends GrantedAuthority> getAuthorities() {
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + this.role);
		return List.of(authority);
	}

	/**
	 * Returns the password used to authenticate the user.
	 *
	 * @return the password
	 */
	@Override
	@JsonIgnore
	public String getPassword() {
		return this.password;
	}

	/**
	 * Returns the username used to authenticate the user. Cannot return
	 * <code>null</code>.
	 *
	 * @return the username (never <code>null</code>)
	 */
	@Override
	public String getUsername() {
		return this.username;
	}

	/**
	 * Indicates whether the user's account has expired. An expired account cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user's account is valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	@Override
	@JsonIgnore
	public boolean isAccountNonExpired() {
		return true;
	}

	/**
	 * Indicates whether the user is locked or unlocked. A locked user cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user is not locked, <code>false</code> otherwise
	 */
	@Override
	@JsonIgnore
	public boolean isAccountNonLocked() {
		return Optional.ofNullable(accountNonLocked).orElse(false);
	}

	/**
	 * Indicates whether the user's credentials (password) has expired. Expired
	 * credentials prevent authentication.
	 *
	 * @return <code>true</code> if the user's credentials are valid (ie non-expired),
	 * <code>false</code> if no longer valid (ie expired)
	 */
	@Override
	@JsonIgnore
	public boolean isCredentialsNonExpired() {
		return true;
	}

	/**
	 * Indicates whether the user is enabled or disabled. A disabled user cannot be
	 * authenticated.
	 *
	 * @return <code>true</code> if the user is enabled, <code>false</code> otherwise
	 */
	@Override
	@JsonIgnore
	public boolean isEnabled() {
		return true;
	}
}
