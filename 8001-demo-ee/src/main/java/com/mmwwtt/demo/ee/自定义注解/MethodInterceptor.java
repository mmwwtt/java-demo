package com.mmwwtt.demo.ee.自定义注解;

import org.springframework.aop.MethodBeforeAdvice;

import java.lang.reflect.Method;

public class MethodInterceptor  implements MethodBeforeAdvice {
    @Override
    public void before(Method method, Object[] args, Object target) throws Throwable {
        if (method.isAnnotationPresent(MyStart.class)) {
            System.out.println("hello");
        }
    }
}