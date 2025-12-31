package com.mmwwtt.demo.ee.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;

/**
 * AOP切面
 * 根据方法路径和方法名称匹配切点
 * 默认顺序  环绕-前置-真正方法-后置-环绕
 */
@Component
@Aspect
@Slf4j
@Order(1)   //同一个切面内，多个前置执行无法确定顺序， 需要分多个切面，通过Order控制执行优先级，值越小，越先执行
public class DemoAspect {
    /**
     * 定义切点
     * execution(* com.mmwwtt.demo.ee.aop.*.*(..))
     *  *:任意一层类路径
     *  *(..):任意方法
     *  method(..): mathod开头的方法
     */
    @Pointcut("execution(* com.mmwwtt.demo.ee.aop.AopController.demo0(..))")
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

    //环绕通知  只有环绕通知能用  joinPoint
    @Around("pointcut()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("Around advice: 方法执行之前");
        Object result = joinPoint.proceed(); // 执行目标方法
        log.info("Around advice: 方法执行之后");
        return result;
    }


    /**
     * 注解方式的AOP
     * @param aspect1
     * @throws Throwable
     */
    @Before("@annotation(aspect1)")
    public void fun1(Aspect1 aspect1) throws Throwable {
        log.info("before - aspect1");
    }

    @Around("@annotation(aspect2)")
    public void fun2(ProceedingJoinPoint joinPoint, Aspect2 aspect2) throws Throwable {
        log.info("around - after- aspect2");
        joinPoint.proceed();
        log.info("around - after- aspect2");
    }

    @After("@annotation(aspect3)")
    public void fun3(Aspect3 aspect3) throws Throwable {
        log.info("after - aspect3");
    }
}
