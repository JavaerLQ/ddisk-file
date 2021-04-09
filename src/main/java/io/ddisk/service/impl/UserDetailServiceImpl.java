package io.ddisk.service.impl;

import io.ddisk.domain.vo.LoginUser;
import io.ddisk.dao.UserRepository;
import io.ddisk.domain.entity.UserEntity;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security配置
 * @Author: Richard.Lee
 * @Date: created by 2021/3/3
 */
@Service
public class UserDetailServiceImpl implements UserDetailsService {

	public static String ROLE = "ROLE_";

	@Autowired
	private UserRepository userRepository;
	/**
	 * Locates the user based on the username. In the actual implementation, the search
	 * may possibly be case sensitive, or case insensitive depending on how the
	 * implementation instance is configured. In this case, the <code>UserDetails</code>
	 * object that comes back may have a username that is of a different case than what
	 * was actually requested..
	 *
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never <code>null</code>)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no
	 *                                   GrantedAuthority
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserEntity userEntity = userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("用户名不存在", new BizException(BizMessage.USERNAME_EXIST)));
		return new LoginUser(userEntity);
	}
}
