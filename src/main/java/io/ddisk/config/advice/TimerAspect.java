package io.ddisk.config.advice;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/6
 */

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

//申明主体类，定义切面主体类
@Slf4j
@Aspect
@Component
public class TimerAspect {

	private final long SEC3 = 3000L;
	private final long SEC2 = 2000L;
	private final long SEC1 = 1000L;

	@Pointcut("execution(* io.ddisk.service..*.*(..))")
	public void serviceTimer() { }

	@Pointcut("execution(* io.ddisk.dao.*.*(..))")
	public void sqlTimer(){}

	@Around("serviceTimer()")
	public Object serviceTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
		// 记录起始时间
		long begin = System.currentTimeMillis();
		// 执行目标方法
		Object result = joinPoint.proceed();
		// 记录操作时间
		long ms = System.currentTimeMillis() - begin;
		if (ms >= SEC3){
			log.warn("{} 执行时间为: {}毫秒", joinPoint.getSignature(), ms);
		}
		return result;
	}

	@Around("sqlTimer()")
	public Object daoTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
		// 记录起始时间
		long begin = System.currentTimeMillis();
		// 执行目标方法
		Object result = joinPoint.proceed();
		// 记录操作时间
		long ms = System.currentTimeMillis() - begin;
		if (ms >= SEC1){
			log.warn("{} 执行时间为: {}毫秒", joinPoint.getSignature(), ms);
		}
		return result;
	}
}
