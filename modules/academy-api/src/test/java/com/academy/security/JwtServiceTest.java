package com.academy.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Comprehensive unit tests for JwtService
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {
    
    @InjectMocks
    private JwtService jwtService;
    
    private String testSecret = "AcademyBackendSecretKeyForJWTTokenGenerationAndValidationMustBeAtLeast256Bits";
    private Long testExpiration = 3600000L; // 1 hour
    
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }
    
    // ========== generateToken(String username, String... roles) Tests ==========
    
    @Test
    @DisplayName("Should generate token with username and roles")
    void generateToken_WithUsernameAndRoles_ReturnsToken() {
        // When
        String token = jwtService.generateToken("test@example.com", "ROLE_USER", "ROLE_ADMIN");
        
        // Then
        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3); // JWT has 3 parts
    }
    
    @Test
    @DisplayName("Should generate token with username only")
    void generateToken_WithUsernameOnly_ReturnsToken() {
        // When
        String token = jwtService.generateToken("test@example.com");
        
        // Then
        assertThat(token).isNotNull();
        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should generate token with null roles")
    void generateToken_WithNullRoles_ReturnsToken() {
        // When
        String token = jwtService.generateToken("test@example.com", (String[]) null);
        
        // Then
        assertThat(token).isNotNull();
    }
    
    @Test
    @DisplayName("Should generate token with empty roles")
    void generateToken_WithEmptyRoles_ReturnsToken() {
        // When
        String token = jwtService.generateToken("test@example.com", new String[0]);
        
        // Then
        assertThat(token).isNotNull();
    }
    
    // ========== generateToken(String username, Map<String, Object> claims) Tests ==========
    
    @Test
    @DisplayName("Should generate token with custom claims")
    void generateToken_WithCustomClaims_ReturnsToken() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("custom", "value");
        claims.put("number", 123);
        
        // When
        String token = jwtService.generateToken("test@example.com", claims);
        
        // Then
        assertThat(token).isNotNull();
        String customValue = jwtService.extractClaim(token, c -> c.get("custom", String.class));
        assertThat(customValue).isEqualTo("value");
    }
    
    // ========== extractUsername Tests ==========
    
    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_FromValidToken_ReturnsUsername() {
        // Given
        String token = jwtService.generateToken("test@example.com");
        
        // When
        String username = jwtService.extractUsername(token);
        
        // Then
        assertThat(username).isEqualTo("test@example.com");
    }
    
    // ========== extractExpiration Tests ==========
    
    @Test
    @DisplayName("Should extract expiration from token")
    void extractExpiration_FromValidToken_ReturnsExpiration() {
        // Given
        String token = jwtService.generateToken("test@example.com");
        
        // When
        Date expiration = jwtService.extractExpiration(token);
        
        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }
    
    // ========== extractClaim Tests ==========
    
    @Test
    @DisplayName("Should extract custom claim from token")
    void extractClaim_FromValidToken_ReturnsClaim() {
        // Given
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", "ROLE_USER,ROLE_ADMIN");
        String token = jwtService.generateToken("test@example.com", claims);
        
        // When
        String roles = jwtService.extractClaim(token, c -> c.get("roles", String.class));
        
        // Then
        assertThat(roles).isEqualTo("ROLE_USER,ROLE_ADMIN");
    }
    
    // ========== validateToken(String token, String username) Tests ==========
    
    @Test
    @DisplayName("Should validate token with matching username")
    void validateToken_WithMatchingUsername_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken("test@example.com");
        
        // When
        Boolean isValid = jwtService.validateToken(token, "test@example.com");
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Should invalidate token with non-matching username")
    void validateToken_WithNonMatchingUsername_ReturnsFalse() {
        // Given
        String token = jwtService.generateToken("test@example.com");
        
        // When
        Boolean isValid = jwtService.validateToken(token, "other@example.com");
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should invalidate expired token")
    void validateToken_WithExpiredToken_ReturnsFalse() {
        // Given
        // Create expired token by setting expiration in past
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test@example.com");
        Date now = new Date();
        Date past = new Date(now.getTime() - 10000); // 10 seconds ago
        
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
            .claims(claims)
            .subject("test@example.com")
            .issuedAt(past)
            .expiration(past)
            .signWith(key)
            .compact();
        
        // When
        Boolean isValid = jwtService.validateToken(expiredToken, "test@example.com");
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    // ========== validateToken(String token) Tests ==========
    
    @Test
    @DisplayName("Should validate valid token without username check")
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtService.generateToken("test@example.com");
        
        // When
        Boolean isValid = jwtService.validateToken(token);
        
        // Then
        assertThat(isValid).isTrue();
    }
    
    @Test
    @DisplayName("Should invalidate expired token without username check")
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Given
        // Create expired token
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", "test@example.com");
        Date past = new Date(System.currentTimeMillis() - 10000);
        
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        String expiredToken = Jwts.builder()
            .claims(claims)
            .subject("test@example.com")
            .issuedAt(past)
            .expiration(past)
            .signWith(key)
            .compact();
        
        // When
        Boolean isValid = jwtService.validateToken(expiredToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should handle invalid token gracefully")
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.token.here";
        
        // When
        Boolean isValid = jwtService.validateToken(invalidToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should handle malformed token gracefully")
    void validateToken_MalformedToken_ReturnsFalse() {
        // Given
        String malformedToken = "not.a.valid.jwt";
        
        // When
        Boolean isValid = jwtService.validateToken(malformedToken);
        
        // Then
        assertThat(isValid).isFalse();
    }
    
    @Test
    @DisplayName("Should handle null token gracefully")
    void validateToken_NullToken_ReturnsFalse() {
        // When
        Boolean isValid = jwtService.validateToken((String) null);
        
        // Then
        assertThat(isValid).isFalse();
    }
}

