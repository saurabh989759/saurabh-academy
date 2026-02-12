package com.academy.controller;

import com.academy.generated.model.AuthResponse;
import com.academy.generated.model.LoginRequest;
import com.academy.generated.model.TokenRequest;
import com.academy.generated.model.TokenValidationResponse;
import com.academy.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for AuthController
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("AuthController Tests")
class AuthControllerTest {
    
    @Mock
    private JwtService jwtService;
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    private AuthController authController;
    
    private LoginRequest loginRequest;
    private Authentication authentication;
    private String testToken;
    
    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setUsername("test@example.com");
        loginRequest.setPassword("password123");
        
        testToken = "eyJhbGciOiJIUzUxMiJ9.test.token";
        
        authentication = new UsernamePasswordAuthenticationToken(
            "test@example.com",
            "password123",
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
    
    // ========== login Tests ==========
    
    @Test
    @DisplayName("Should login successfully and return token")
    void login_WhenValidCredentials_ReturnsToken() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(jwtService.generateToken("test@example.com", "ROLE_USER"))
            .thenReturn(testToken);
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isEqualTo(testToken);
        assertThat(response.getBody().getType()).isEqualTo("Bearer");
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateToken("test@example.com", "ROLE_USER");
    }
    
    @Test
    @DisplayName("Should extract role from authentication authorities")
    void login_WithMultipleRoles_ExtractsFirstRole() {
        // Given
        Authentication authWithMultipleRoles = new UsernamePasswordAuthenticationToken(
            "test@example.com",
            "password123",
            Arrays.asList(
                new SimpleGrantedAuthority("ROLE_ADMIN"),
                new SimpleGrantedAuthority("ROLE_USER")
            )
        );
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authWithMultipleRoles);
        when(jwtService.generateToken(eq("test@example.com"), eq("ROLE_ADMIN")))
            .thenReturn(testToken);
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(jwtService).generateToken("test@example.com", "ROLE_ADMIN");
    }
    
    @Test
    @DisplayName("Should use default role when no authorities")
    void login_WithNoAuthorities_UsesDefaultRole() {
        // Given
        Authentication authNoRoles = new UsernamePasswordAuthenticationToken(
            "test@example.com",
            "password123",
            Collections.emptyList()
        );
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authNoRoles);
        when(jwtService.generateToken(eq("test@example.com"), eq("ROLE_USER")))
            .thenReturn(testToken);
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(jwtService).generateToken("test@example.com", "ROLE_USER");
    }
    
    @Test
    @DisplayName("Should return 401 when authentication fails")
    void login_WhenBadCredentials_Returns401() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        verify(jwtService, never()).generateToken(anyString(), anyString());
    }
    
    @Test
    @DisplayName("Should return 401 when user not found")
    void login_WhenUserNotFound_Returns401() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new org.springframework.security.core.userdetails.UsernameNotFoundException("User not found"));
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    @Test
    @DisplayName("Should handle unexpected exceptions")
    void login_WhenUnexpectedException_Returns401() {
        // Given
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new RuntimeException("Unexpected error"));
        
        // When
        ResponseEntity<AuthResponse> response = authController.login(loginRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
    
    // ========== validateToken Tests ==========
    
    @Test
    @DisplayName("Should validate token successfully")
    void validateToken_WhenValid_ReturnsValidResponse() {
        // Given
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(testToken);
        when(jwtService.validateToken(testToken)).thenReturn(true);
        when(jwtService.extractUsername(testToken)).thenReturn("test@example.com");
        
        // When
        ResponseEntity<TokenValidationResponse> response = authController.validateToken(tokenRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValid()).isTrue();
        assertThat(response.getBody().getUsername()).isEqualTo("test@example.com");
    }
    
    @Test
    @DisplayName("Should invalidate token when invalid")
    void validateToken_WhenInvalid_ReturnsInvalidResponse() {
        // Given
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(testToken);
        when(jwtService.validateToken(testToken)).thenReturn(false);
        
        // When
        ResponseEntity<TokenValidationResponse> response = authController.validateToken(tokenRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValid()).isFalse();
        assertThat(response.getBody().getUsername()).isNull();
    }
    
    @Test
    @DisplayName("Should handle validation exception gracefully")
    void validateToken_WhenException_ReturnsInvalidWithError() {
        // Given
        TokenRequest tokenRequest = new TokenRequest();
        tokenRequest.setToken(testToken);
        when(jwtService.validateToken(testToken)).thenThrow(new RuntimeException("Validation error"));
        
        // When
        ResponseEntity<TokenValidationResponse> response = authController.validateToken(tokenRequest);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().isValid()).isFalse();
        assertThat(response.getBody().getError()).isNotNull();
    }
}

