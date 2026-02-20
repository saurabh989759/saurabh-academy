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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController implements AuthenticationApi {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest loginRequest) {
        try {
            log.debug("Login attempt for: {}", loginRequest.getUsername());

            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String role = auth.getAuthorities().stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

            String token = jwtService.generateToken(loginRequest.getUsername(), role);

            AuthResponse response = new AuthResponse();
            response.setToken(token);
            response.setType("Bearer");

            log.info("Login successful for user: {} role: {}", loginRequest.getUsername(), role);
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException | UsernameNotFoundException ex) {
            log.warn("Login rejected for user: {} — {}", loginRequest.getUsername(), ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception ex) {
            log.error("Unexpected error during login for: {}", loginRequest.getUsername(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Override
    public ResponseEntity<TokenValidationResponse> validateToken(TokenRequest tokenRequest) {
        TokenValidationResponse response = new TokenValidationResponse();
        try {
            boolean valid = jwtService.validateToken(tokenRequest.getToken());
            response.setValid(valid);
            if (valid) {
                response.setUsername(jwtService.extractUsername(tokenRequest.getToken()));
            }
        } catch (Exception ex) {
            response.setValid(false);
            response.setError(ex.getMessage());
        }
        return ResponseEntity.ok(response);
    }
}
