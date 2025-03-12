package com.mmwwtt.demo.jwt.demo;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class JwtGenerator {
    public static String generateToken(String secret, String subject, long expirationMillis) {
        // 定义签名算法，这里使用 HMAC256
        Algorithm algorithm = Algorithm.HMAC256(secret);
        // 生成 JWT
        String token = "Bearer " + JWT.create()
                .withSubject(subject)
                .withClaim("role", "role") // 添加用户角色信息
                .withClaim("username", "username") // 添加用户名信息
                .withExpiresAt(new Date(System.currentTimeMillis() + expirationMillis))
                .sign(algorithm);
        return token;
    }

    public static void main(String[] args) {
        log.info(JwtGenerator.generateToken("qqwwee", "userId",999999999));
    }
}
