package com.mmwwtt.demo.ee.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@Order(2)
public class DemoAspect2 {
    @Around("@annotation(aspect2)")
    public void fun2(ProceedingJoinPoint joinPoint, Aspect2 aspect2) throws Throwable {
        log.info("around - after- aspect2");
        joinPoint.proceed();
        log.info("around - after- aspect2");
    }

}
