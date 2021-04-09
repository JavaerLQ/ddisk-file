package io.ddisk.config.advice;

import io.ddisk.domain.vo.base.ErrorVO;
import io.ddisk.exception.BizException;
import io.ddisk.exception.msg.BizMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/2/17
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandlerAdvice {


	/**-------- 通用异常处理方法 --------**/
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<ErrorVO> error(Exception e) {
		log.error("服务错误", e);
		ErrorVO vo = ErrorVO.builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).msg(e.getMessage()).build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(vo);
	}

	/**-------- 自定义定异常处理方法 --------**/
	@ExceptionHandler(BizException.class)
	@Order(Ordered.HIGHEST_PRECEDENCE)
	@ResponseBody
	public ResponseEntity<ErrorVO> error(BizException e) {
		if (BizMessage.USER_NOT_LOGIN.getCode().equals(e.getCode())){
			log.warn(e.getMessage(), e.getCause());
		}else{
			log.error("业务异常" , e);
		}
		ErrorVO vo = ErrorVO.builder().status(e.getCode()).msg(e.getMessage()).build();
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(vo);
	}

	/**
	 * 登陆时 用户名或密码错误
	 */
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorVO> badCredentialsException(BadCredentialsException e){
		log.error("用户登录异常", e);
		ErrorVO vo = ErrorVO.builder().status(BizMessage.USER_LOGIN_ERROR.getCode()).msg(e.getMessage()).build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(vo);
	}

	/**
	 * 请求参数格式不正确，校验不通过
	 */
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ResponseEntity<ErrorVO> handle(ConstraintViolationException e) {
		log.error("用户提交参数格式不正确", e);
		Map<Path, String> map = e.getConstraintViolations().stream().collect(Collectors.toMap(ConstraintViolation::getPropertyPath, ConstraintViolation::getMessage));
		ErrorVO vo = ErrorVO.builder().status(BizMessage.REQUEST_PARAMS_INVALIDATE.getCode()).msg(BizMessage.REQUEST_PARAMS_INVALIDATE.getMessage()).data(map).build();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(vo);
	}
}
