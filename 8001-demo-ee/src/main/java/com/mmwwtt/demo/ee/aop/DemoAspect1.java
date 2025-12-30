package com.mmwwtt.demo.ee.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * AOP切面
 * 根据注解匹配切点
 */
@Component
@Aspect
@Slf4j
public class DemoAspect1 {
    // 前置通知
    @Before("@annotation(aspect1)")
    public void fun1(ProceedingJoinPoint joinPoint, Aspect1 aspect1) {
        log.info("aspect1");
    }

    @Before("@annotation(aspect2)")
    public void fun2(ProceedingJoinPoint joinPoint, Aspect2 aspect2) {
        log.info("aspect2");
    }

    @Before("@annotation(aspect3)")
    public void fun3(ProceedingJoinPoint joinPoint, Aspect3 aspect3) {
        log.info("aspect3");
    }
}
