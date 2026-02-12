package com.academy.controller;

import com.academy.generated.api.AuthenticationApi;
import com.academy.generated.model.AuthResponse;
import com.academy.generated.model.LoginRequest;
import com.academy.generated.model.TokenRequest;
import com.academy.generated.model.TokenValidationResponse;
import com.academy.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RestController;

/**
 * Authentication controller implementing generated AuthenticationApi interface
 * Uses generated request/response models from OpenAPI
 * Handles login and JWT token validation
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthenticationApi {
    
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        try {
            log.debug("Attempting login for user: {}", loginRequest.getUsername());
            
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            log.debug("Authentication successful for user: {}", loginRequest.getUsername());
            
            // Extract role from authentication
            String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(auth -> auth.getAuthority())
                .orElse("ROLE_USER");
            
            // Generate JWT token
            String token = jwtService.generateToken(
                loginRequest.getUsername(),
                role
            );
            
            // Create response using generated model
            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setType("Bearer");
            
            log.info("User logged in successfully: {} with role: {}", loginRequest.getUsername(), role);
            return ResponseEntity.ok(response);
            
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            log.warn("Login failed - Bad credentials for user: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (org.springframework.security.core.userdetails.UsernameNotFoundException e) {
            log.warn("Login failed - User not found: {}", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("Login failed for user: {} - Unexpected error", loginRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    
    @Override
    public ResponseEntity<TokenValidationResponse> validateToken(TokenRequest tokenRequest) {
        TokenValidationResponse response = new TokenValidationResponse();
        
        try {
            boolean isValid = jwtService.validateToken(tokenRequest.getToken());
            String username = isValid ? jwtService.extractUsername(tokenRequest.getToken()) : null;
            
            response.setValid(isValid);
            if (isValid) {
                response.setUsername(username);
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.setValid(false);
            response.setError(e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
}

