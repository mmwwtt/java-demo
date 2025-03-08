package com.mmwwtt.demo.ee.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/service/demo/ee/jwt")
@Slf4j
public class JwtDemo {
    private static final String SECRET = "qqwwee";

    @PostMapping("/jwtDemo")
    public String secureResource(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                Algorithm algorithm = Algorithm.HMAC256(SECRET);
                JWTVerifier verifier = JWT.require(algorithm)
                        .build();
                verifier.verify(token);

                // 验证并解析 JWT
                DecodedJWT jwt = verifier.verify(token);
                // 获取用户信息
                String subject = jwt.getSubject();
                String username = jwt.getClaim("username").asString();
                String role = jwt.getClaim("role").asString();
                log.info("Subject: {}", subject);
                log.info("Username: {}" , username);
                log.info("Role: {}" , role);
                // 验证通过，返回安全资源
                return "This is a secure resource";
            } catch (JWTVerificationException e) {
                log.info("Invalid token. Reason: {}", e.getMessage());
                // 可以根据具体的错误信息进一步排查
                if (e.getMessage().contains("The Token has expired")) {
                    log.info("Token has expired");
                } else if (e.getMessage().contains("Signature verification failed")) {
                    log.info("Signature verification failed, check secret");
                }
            }
        } else {
            return "Missing or invalid Authorization header";
        }
        return "This is a secure resource";
    }
}
