package com.mmwwtt.demo.jwt.demo;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/jwt")
@Slf4j
public class JwtDemo {
    private static final String SECRET = "qqwwee";


    @Resource
    public JwtUtil jwtUtil;

    @PostMapping("/jwtDemo")
    public String secureResource(@RequestHeader("Authorization") String authorizationHeader) {

        return "This is a secure resource";
    }
}
