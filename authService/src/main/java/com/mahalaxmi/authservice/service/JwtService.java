package com.mahalaxmi.authservice.service;

import io.jsonwebtoken.Claims;

import javax.crypto.SecretKey;

public interface JwtService {
    public String generateToken(String email);
    public String generateRefreshToken(String email);
    public Claims verifyToken(String token);
    public String extractEmail(String token);
    public boolean isExpired(String token);
}
