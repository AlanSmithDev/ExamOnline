package com.sdut.examonline.common.util;

import com.sdut.examonline.common.constant.JwtConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;

public class JwtTokenUtils {

    public static String createToken(Long userId, String userName, String role, Integer platform) {
        return Jwts.builder()
                .signWith(SignatureAlgorithm.HS512, JwtConstants.APPSECRET_KEY)
                .claim(JwtConstants.USER_ID, String.valueOf(userId))
                .claim(JwtConstants.USER_NAME, userName)
                .claim(JwtConstants.ROLE, role)
                .claim(JwtConstants.PLATFORM, platform)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtConstants.EXPIRATION))
                .compact();
    }

    public static String getUserName(String token) {
        return (String) getTokenBody(token).get(JwtConstants.USER_NAME);
    }

    public static Long getUserId(String token) {
        Object value = getTokenBody(token).get(JwtConstants.USER_ID);
        return value == null ? null : Long.parseLong((String) value);
    }

    public static String getUserRole(String token) {
        return (String) getTokenBody(token).get(JwtConstants.ROLE);
    }

    public static Integer getPlatform(String token) {
        Object value = getTokenBody(token).get(JwtConstants.PLATFORM);
        return value == null ? -1 : (Integer) value;
    }

    private static Claims getTokenBody(String token) {
        return Jwts.parser().setSigningKey(JwtConstants.APPSECRET_KEY).parseClaimsJws(token).getBody();
    }

    public static boolean isExpiration(String token) {
        try {
            return getTokenBody(token).getExpiration().before(new Date());
        } catch (Exception e) {
            return true;
        }
    }

}

