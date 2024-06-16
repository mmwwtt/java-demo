package com.mmwwtt.demo.ee.starter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.mmwwtt.demo.ee.*")
public class DemoEEStarter {

    public static void main(String[] args) {
        SpringApplication.run(DemoEEStarter.class, args);
    }

}
