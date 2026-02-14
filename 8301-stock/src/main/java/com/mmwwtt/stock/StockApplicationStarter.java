package com.mmwwtt.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.mmwwtt.stock")
@SpringBootApplication
public class StockApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(StockApplicationStarter.class, args);
    }
}