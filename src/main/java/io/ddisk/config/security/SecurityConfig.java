package io.ddisk.config.security;

import io.ddisk.domain.vo.LoginUser;
import io.ddisk.domain.vo.base.ErrorVO;
import io.ddisk.exception.msg.BaseMessage;
import io.ddisk.exception.msg.BizMessage;
import io.ddisk.utils.ResponseUtils;
import io.ddisk.utils.SpringWebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpServletResponse;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/3/3
 */
@Slf4j
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	@Qualifier("userDetailServiceImpl")
	private UserDetailsService userDetailsService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * session会话监听交给Spring容器管理
	 * @return
	 */
	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
				.antMatchers("/swagger-ui/**", "/webjars/**", "/v3/**", "/api-docs/**")
				.antMatchers("/js/**", "/css/**", "/index.html", "/img/**", "/fonts/**")
		;
	}

	@Override
	public void configure(HttpSecurity http) throws Exception {
		http
				.authorizeRequests().antMatchers("/user/login", "/user/register", "/user/check", "user/exist/**", "/user/reset/passwd/**").permitAll()
				.anyRequest().authenticated()
				.and().rememberMe()
				.and().formLogin().loginProcessingUrl("/user/login").permitAll()
				.successHandler((request, response, authentication) -> {
					// 登录成功
					LoginUser user = (LoginUser) authentication.getPrincipal();
					WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
					log.info("用户登录成功[Username: {}, Role: {}, IP: {}]", user.getUsername(), user.getRole(), SpringWebUtils.getRemoteAddr());
					ResponseUtils.build(response).ok().write(authentication.getPrincipal()).send();
				})
				.failureHandler((request, response, exception) -> {
					// 登录失败
					BaseMessage msg = BizMessage.SYSTEM_EXCEPTION;
					if (exception instanceof LockedException) {
						msg = BizMessage.ACCOUNT_LOCKED;
					} else if (exception instanceof CredentialsExpiredException) {
						msg = BizMessage.CREDENTIALS_EXPIRED;
					} else if (exception instanceof AccountExpiredException) {
						msg = BizMessage.ACCOUNT_EXPIRED;
					} else if (exception instanceof DisabledException) {
						msg = BizMessage.ACCOUNT_DISABLED;
					} else if (exception instanceof BadCredentialsException) {
						msg = BizMessage.BAD_CREDENTIALS;
					}
					log.warn(msg.getMessage(), exception);
					ErrorVO vo = ErrorVO.builder().status(msg.getCode()).msg(msg.getMessage()).build();
					ResponseUtils.build(response).badRequest().write(vo).send();
				})
				.and().logout().logoutUrl("/user/logout")
				.logoutSuccessHandler((request, response, authentication) -> {
					// 退出登录成功
					LoginUser user = (LoginUser) authentication.getPrincipal();
					WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
					log.info("用户注销成功[Username: {}, Role: {}, IP: {}]", user.getUsername(), user.getRole(), SpringWebUtils.getRemoteAddr());
					ResponseUtils.build(response).ok().write("注销成功！").send();
				})
				.and()
				.exceptionHandling()
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					//权限拒绝处理逻辑
					log.warn("权限不足", accessDeniedException);
					ErrorVO vo = ErrorVO.builder().status(HttpServletResponse.SC_UNAUTHORIZED).msg("对不起，您没有权限访问该资源，请联系管理员！").build();
					ResponseUtils.build(response).status(HttpServletResponse.SC_UNAUTHORIZED).write(vo).send();
				})
				.authenticationEntryPoint((request, response, authException) -> {
					//匿名用户访问无权限资源时的异常处理
					log.warn("阻止匿名用户");
					ErrorVO vo = ErrorVO.builder().status(HttpServletResponse.SC_FORBIDDEN).msg("该资源只有登录用户才能访问！").build();
					ResponseUtils.build(response).status(HttpServletResponse.SC_FORBIDDEN).write(vo).send();
				})
				.and().csrf().disable()
				.sessionManagement()
				.maximumSessions(1)

		;
	}

	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
}
