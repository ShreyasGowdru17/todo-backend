package com.raksh.ToDo.service;

import com.raksh.ToDo.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey=null;

    public String generateToken(User user){
        Map<String ,Object> claims=new HashMap<>();

        return Jwts
                .builder()
                .claims()
                .add(claims)
                .subject(user.getUserName())
                .issuer("ToDo")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000)) // 24 hours
                .and()
                .signWith(generateKey())
                .compact();
    }

    public SecretKey generateKey(){
        byte[] decode = Decoders.BASE64.decode(getSecretKey());
        return Keys.hmacShaKeyFor(decode);
    }

    public String getSecretKey(){
        return secretKey;
    }

    public String extractUserName(String token) {
        return extractClaims(token, Claims::getSubject);

    }

    private <T>T extractClaims(String token, Function<Claims,T> claimsResolver) {
        Claims claims=extractClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractClaims(String token){
        if (token == null || token.trim().isEmpty() || token.split("\\.").length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format: " + token);
        }

        return Jwts
                .parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName=extractUserName(token);
        return (userName.equals(userDetails.getUsername())&& !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaims(token,Claims::getExpiration);
    }
}
