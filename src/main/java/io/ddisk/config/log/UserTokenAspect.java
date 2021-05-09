package io.ddisk.config.log;

import io.ddisk.domain.entity.UserTokenEntity;
import io.ddisk.utils.SpringWebUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * @author lee
 * @date 2021/5/9
 */
@Slf4j
@Aspect
@Configuration
public class UserTokenAspect {

    @Pointcut("execution(* io.ddisk.service.impl.UserTokenServiceImpl.addToken(io.ddisk.domain.entity.UserTokenEntity, String, java.util.Date))")
    public void addTokenCut() {
    }

    @After("addTokenCut()")
    public void addToken(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        String email = ((UserTokenEntity) args[0]).getEmail();
        log.info("用户[{}]添加令牌[{}]", Objects.isNull(SpringWebUtils.getRequestUser()) ? email : SpringWebUtils.requireLogin().getUsername(), args[0]);
    }

    @Pointcut("execution(* io.ddisk.service.impl.UserTokenServiceImpl.useUserToken(String, String, io.ddisk.domain.enums.TokenTypeEnum))")
    public void userTokenCut() {
    }

    @After("userTokenCut()")
    public void useToken(JoinPoint joinPoint) {

        Object[] args = joinPoint.getArgs();
        log.info("用户[{}]使用类型[{}]的令牌[{}]", Objects.isNull(SpringWebUtils.getRequestUser()) ? args[0] : SpringWebUtils.requireLogin().getUsername(), args[2], args[1]);
    }
}
