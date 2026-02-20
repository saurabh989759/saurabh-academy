package com.academy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret:AcademyBackendSecretKeyForJWTTokenGenerationAndValidationMustBeAtLeast256Bits}")
    private String secretKey;

    @Value("${jwt.expiration:86400000}")
    private Long tokenTtlMs;

    // -------------------------------------------------------------------------
    // Token generation
    // -------------------------------------------------------------------------

    public String generateToken(String username, String... roles) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", username);
        if (roles != null && roles.length > 0) {
            claims.put("roles", String.join(",", roles));
        }
        return buildToken(claims, username);
    }

    public String generateToken(String username, Map<String, Object> claims) {
        return buildToken(claims, username);
    }

    // -------------------------------------------------------------------------
    // Token validation
    // -------------------------------------------------------------------------

    public Boolean validateToken(String token, String username) {
        return username.equals(extractUsername(token)) && !isExpired(token);
    }

    public Boolean validateToken(String token) {
        try {
            return !isExpired(token);
        } catch (Exception ex) {
            log.warn("Token validation failed: {}", ex.getMessage());
            return false;
        }
    }

    // -------------------------------------------------------------------------
    // Claims extraction
    // -------------------------------------------------------------------------

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        return resolver.apply(parseAllClaims(token));
    }

    // -------------------------------------------------------------------------
    // Private internals
    // -------------------------------------------------------------------------

    private String buildToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(now)
            .expiration(new Date(now.getTime() + tokenTtlMs))
            .signWith(signingKey())
            .compact();
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(signingKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    private boolean isExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }
}
