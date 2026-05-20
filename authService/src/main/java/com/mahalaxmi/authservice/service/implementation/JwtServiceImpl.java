package com.mahalaxmi.authservice.service.implementation;

import com.mahalaxmi.authservice.entity.Users;
import com.mahalaxmi.authservice.repository.AuthRepository;
import com.mahalaxmi.authservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {
    @Value("${SECRET_KEY}")
    private String SECRET_KEY;

    @Autowired
    AuthRepository authRepository;

    @Override
    public String generateToken(String email){
        Users user = authRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HashMap<String, Object> claims = new HashMap<>();

        claims.put("roles", List.of(user.getRole()));
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis()+ 1000*60*60))
                .claims(claims)
                .signWith(getKey())
                .compact();
    }

    public String generateRefreshToken(String email) {
        return Jwts.builder()
                .subject(email)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000)) // 7 days
                .signWith(getKey())
                .compact();
    }

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Override
    public Claims verifyToken(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public String extractEmail(String token) {
        return verifyToken(token).getSubject();
    }

    @Override
    public boolean isExpired(String token) {
        return verifyToken(token).getExpiration().before(new Date());
    }

}
