package com.wwmmtt.demo.springmvc.demo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
public class LttRedisProperties {
    private String name;
    @Bean
    public String getTestStr() {
        String str = name;
        return str;
    }
}
