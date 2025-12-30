package com.mmwwtt.demo.ee.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * AOP切面
 * 根据方法路径和方法名称匹配切点
 */
@Component
@Aspect
@Slf4j
public class DemoAspect {
    /**
     * 定义切点
     *  *:任意一层类路径
     *  *(..):任意方法
     *  method(..): mathod开头的方法
     */
    @Pointcut("execution(* com.mmwwtt.demo.ee.aop.*.*(..))")
    public void pointcut() {}

    // 前置通知
    @Before("pointcut()")
    public void beforeAdvice() {
        log.info("Before advice: 方法执行之前");
    }

    // 后置通知
    @After("pointcut()")
    public void afterAdvice() {
        log.info("After advice: 方法执行之后");
    }

    //异常通知
    @AfterThrowing(pointcut = "pointcut()", throwing = "ex")
    public void afterThrowingAdvice(Exception ex) {
        log.info("After throwing advice: 捕获异常 " + ex.getMessage());
    }

    //环绕通知
    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around advice: 方法执行之前");
        Object result = joinPoint.proceed(); // 执行目标方法
        log.info("Around advice: 方法执行之后");
        return result;
    }
}
