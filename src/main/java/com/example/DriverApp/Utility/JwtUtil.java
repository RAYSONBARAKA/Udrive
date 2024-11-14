package com.example.DriverApp.Utility;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

public class JwtUtil {

    Logger log = LoggerFactory.getLogger(getClass());

    private static final String SECRET_KEY = "e03c24c66303d84372e3c4a057a93e70664f4501e7116d6b5ae544173fa26b5a";
    private static final long EXPIRATION_TIME = 3600000; 

    // Convert Base64 encoded key to object key and generate a signing key using HMAC SHA
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Generate token with a specific expiration period 
    public String generateTokenWithExpiration(String subject, int expirationDays) {
        log.info("Generating JWT for customer with " + expirationDays + "-day expiration");
        long expirationMillis = expirationDays * 24 * 60 * 60 * 1000L; 
        return Jwts.builder()
                .setSubject(subject)
                .claim("userType", "customer")  
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))  
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Validate the token, ensuring it belongs to a customer and is not expired
    public boolean validateToken(String token, String email) {
        Claims claims = getClaimsFromToken(token);
        String userType = claims.get("userType", String.class);

        // Ensure the token has the correct userType and the email matches
        return getEmailFromToken(token).equals(email) && "customer".equals(userType) && !isTokenExpired(token);
    }

    // Extract email from the token
    public String getEmailFromToken(String token) {
        log.info("Attempting to get email from token");
        return getClaimsFromToken(token).getSubject();
    }

    // Extract claims from the token
    private Claims getClaimsFromToken(String token) {
        log.info("Attempting to get claims from token");
        return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token).getBody();
    }

    // Check if the token has expired
    private boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    // Extract email using helper method
    public String extractEmail(String token) {
        return getEmailFromToken(token);
    }
}
