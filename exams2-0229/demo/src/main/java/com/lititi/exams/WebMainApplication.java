package com.lititi.exams;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebMainApplication {
    public static void main(String[] args) {
        String env = System.getenv("springProfileActive");
        String locationPath = "spring.config.location=optional:classpath:/" + env + "/";
        new SpringApplicationBuilder(WebMainApplication.class).properties(locationPath).build().run(args);
        System.out.println("Hello world!");
    }
}