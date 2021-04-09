package io.ddisk.utils;

import io.ddisk.domain.vo.LoginUser;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import jodd.util.StringPool;
import jodd.util.StringUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * @author lee
 * @date 2021/2/21
 */
public class SpringWebUtils {
	private static final String UNKNOWN = "UNKNOWN";


	/**
	 * 获取用户真实IP
	 *
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {

		String ip = request.getHeader("x-forwarded-for");

		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		if (ip.contains(StringPool.COMMA)) {
			return ip.split(StringPool.COMMA)[0];
		} else {
			return ip;
		}
	}

	/**
	 * 从SpringSecurity上下文中获取请求用户
	 *
	 * @return
	 */
	public static LoginUser getRequestUser() {
		Object principal = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
				.map(Authentication::getPrincipal).orElse(null);
		if (principal instanceof LoginUser) {
			return (LoginUser) principal;
		}
		return null;
	}

	/**
	 * 获取用户登录信息，如用户未登录，抛出用户未登录异常
	 *
	 * @return
	 */
	public static LoginUser requireLogin() {
		return Optional.ofNullable(getRequestUser()).orElseThrow(() -> new BizException(BizMessage.USER_NOT_LOGIN));
	}


	/**
	 * 获取response对象
	 * @return
	 */
	public static HttpServletResponse getResponse(){
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		assert requestAttributes != null;
		return requestAttributes.getResponse();
	}

	/**
	 * 获取request对象
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		assert requestAttributes != null;
		return requestAttributes.getRequest();
	}
}
