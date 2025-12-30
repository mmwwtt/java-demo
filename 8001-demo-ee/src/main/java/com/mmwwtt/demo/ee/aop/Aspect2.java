package com.mmwwtt.demo.ee.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 */
// 定义注解的生命周期为运行时
@Retention(RetentionPolicy.RUNTIME)
// 定义注解只能应用于方法
@Target(ElementType.METHOD)
public @interface Aspect2 {
    String value() default "";
}