package io.ddisk.config.log;

/**
 * @Author: Richard.Lee
 * @Date: created by 2021/4/6
 */

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;


@Slf4j
@Aspect
@Configuration
public class TimerAspect {

	@Pointcut("execution(* io.ddisk.dao.*.*(..))")
	public void sqlTimer(){}

	@Around("sqlTimer()")
	public Object daoTimeLog(ProceedingJoinPoint joinPoint) throws Throwable {
		// 记录起始时间
		long begin = System.currentTimeMillis();
		// 执行目标方法
		Object result = joinPoint.proceed();
		// 记录操作时间
		long ms = System.currentTimeMillis() - begin;
		if (ms >= 500L){
			log.warn("{} 执行时间为: {}毫秒", joinPoint.getSignature(), ms);
		}
		return result;
	}
}
