package com.mmwwtt.demo.ddd.starter;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.mmwwtt.demo.ddd.domain.dao")
@ComponentScan("com.mmwwtt.demo.ddd.*")
public class DddDemo8001Starter {

    public static void main(String[] args) {
        SpringApplication.run(DddDemo8001Starter.class, args);
    }

}
