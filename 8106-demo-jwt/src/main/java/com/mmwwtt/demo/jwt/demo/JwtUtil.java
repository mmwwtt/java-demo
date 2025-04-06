package com.mmwwtt.demo.jwt.demo;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class JwtUtil {

    private static final String SECRET_KEY_STRING = "key_hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh";
    private static final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY_STRING.getBytes());
    public static void main(String[] args) {
        generateToken("小明");
    }
    public static void generateToken(String username) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + 99999999L);
        String jwt = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(currentDate)
                .setExpiration(expireDate)
                .signWith(key)
                .compact();
        System.out.println(jwt);
    }

    public String getUsername(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith((SecretKey) key)
                    .build()
                    .parse(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
