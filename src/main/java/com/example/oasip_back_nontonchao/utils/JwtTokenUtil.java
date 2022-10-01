package com.example.oasip_back_nontonchao.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtTokenUtil implements Serializable {
    private static final long serialVersionUID = -2550185165626007488L;

    public static final long JWT_TOKEN_VALIDITY = (60 * 60) / 2; // 30 mins
    //public static final long JWT_TOKEN_VALIDITY = 60; // 1 mins

    public static final long JWT_TOKEN_VALIDITY_REFRESH = 24 * (60 * 60); // 1 day
    //public static final long JWT_TOKEN_VALIDITY_REFRESH = 300; // 5 mins

    @Value("${jwt.secret}")
    private String secret;


    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public String getRoleFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().get("role").toString();
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }


    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public String generateToken(UserDetails userDetails, String name) {
        Map<String, Object> claims = new HashMap<>();
        HashMap<String, Object> payload = new HashMap<>();
        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();
        claims.put("name", name);
        claims.put("role", (roles.stream().findFirst().get()).toString());
        return doGenerateToken(claims, userDetails.getUsername(), roles, name, 0);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, Collection<? extends GrantedAuthority> roles, String name, long time) {
        if (time == 1) {
            time = JWT_TOKEN_VALIDITY_REFRESH;
        } else {
            time = JWT_TOKEN_VALIDITY;
        }
        claims.put("name", name);
        claims.put("role", ((GrantedAuthority) roles.stream().findFirst().get()).toString());
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + time * 1000)).signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    public String generateRefreshToken(UserDetails userDetails, String name) {
        HashMap<String, Object> payload = new HashMap<>();
        return doGenerateToken(payload, userDetails.getUsername(), userDetails.getAuthorities(), name, 1);
    }

    public String doGenerateRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis())).setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY * 1000)).signWith(SignatureAlgorithm.HS512, secret).compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

}
