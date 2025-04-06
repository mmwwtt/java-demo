package com.mmwwtt.demo.jwt.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jwt")
@Slf4j
public class JwtDemo {
    private static final String SECRET = "qqwwee";


    @Autowired
    public JwtUtil jwtUtil;

    @PostMapping("/jwtDemo")
    public String secureResource(@RequestHeader("Authorization") String authorizationHeader) {

        return "This is a secure resource";
    }
}
