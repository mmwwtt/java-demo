package com.mmwwtt.demo.security.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/public/**").permitAll() // 公开接口
                        .requestMatchers("/admin/**").hasRole("ADMIN") // 需要 ADMIN 角色
                        .anyRequest().authenticated() // 其他请求需要认证
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/home", true) // 登录成功跳转
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/public") // 登出后跳转
                );
        return http.build();
    }
}