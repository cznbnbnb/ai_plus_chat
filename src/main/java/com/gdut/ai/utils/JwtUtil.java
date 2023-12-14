package com.gdut.ai.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;

import java.util.Date;

public class JwtUtil {


    private static String SECRET_KEY = "NGIzZGFkMkmkdnknNIKNDSKSAM"; // 选择一个密钥
    private static final long EXPIRATION_TIME = 86400000; // 设置过期时间，例如24小时

    public static String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }
}

