package com.mmwwtt.stock;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StockApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(StockApplicationStarter.class, args);
    }
}