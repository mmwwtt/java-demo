package com.mmwwtt.demo.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mmwwtt.demo.jwt")
public class DemoJwtStarter {
    public static void main(String[] args) {
        SpringApplication.run(DemoJwtStarter.class, args);
    }
}
