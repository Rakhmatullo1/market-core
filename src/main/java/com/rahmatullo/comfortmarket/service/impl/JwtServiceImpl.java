package com.rahmatullo.comfortmarket.service.impl;

import com.rahmatullo.comfortmarket.service.JwtService;
import com.rahmatullo.comfortmarket.service.exception.EmptyFieldException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final static String KEY = "keykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykeykey";

    @Override
    public String extractUsername(String token) {
        if(Objects.isNull(token) || StringUtils.isEmpty(token)){
            log.warn("Token is empty");
            throw new EmptyFieldException("Token must be not empty");
        }

        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        if(Objects.isNull(token) || token.isEmpty()){
            throw new EmptyFieldException("Token cannot be Null or empty");
        }
        String username = extractUsername(token);
        return Objects.equals(username, userDetails.getUsername()) && !isTokenExpired(token);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    @Override
    public String generateToken(Map<String, String> extraClaims, UserDetails userDetails) {
       return Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(userDetails.getUsername())
                .setExpiration(new Date(System.currentTimeMillis()+ 15*60*1000))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(SignatureAlgorithm.HS256, getKey())
                .compact();
    }

    private boolean isTokenExpired(String token) {
        return getExpirationDate(token).before(new Date(System.currentTimeMillis()));
    }

    private Date getExpirationDate(String token) {
        return extractClaim(token, Claims::getExpiration);
    }


    private <T> T extractClaim(String token, Function<Claims, T> handler){
        Claims claims = extractAllClaims(token);
        return handler.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getKey() {
        byte[] bytes = KEY.getBytes();
        return Keys.hmacShaKeyFor(bytes);
    }
}
