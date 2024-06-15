package com.mmwwtt.demo.ddd.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.mwt.ddd_demo_8001.domain.dao")
@ComponentScan("com.mwt.ddd_demo_8001.*")
public class DddDemo8001Starter {

    public static void main(String[] args) {
        SpringApplication.run(DddDemo8001Starter.class, args);
    }

}
