package com.mmwwtt.demo.ee.自定义注解;

import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InterceptorConfig {
    @Bean
    public DefaultPointcutAdvisor printHelloAdvisor() {
        return new DefaultPointcutAdvisor(
                new AnnotationMatchingPointcut(null, MyStart.class),
                new MethodInterceptor()
        );
    }
}