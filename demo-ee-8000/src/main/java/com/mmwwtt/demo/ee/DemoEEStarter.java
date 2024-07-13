package com.mmwwtt.demo.ee;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mmwwtt.demo.*")
@MapperScan("com.mmwwtt.demo.ee")//mapper包所在的路径
public class DemoEEStarter {

    public static void main(String[] args) {
        SpringApplication.run(DemoEEStarter.class, args);
    }

}
