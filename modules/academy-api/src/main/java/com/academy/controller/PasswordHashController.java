package com.academy.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Temporary controller to generate BCrypt password hash
 * TODO: Remove this in production - it's only for development
 */
@RestController
@RequestMapping("/api/dev")
@RequiredArgsConstructor
@Slf4j
public class PasswordHashController {
    
    private final PasswordEncoder passwordEncoder;
    
    @GetMapping("/generate-hash")
    public Map<String, String> generateHash(@RequestParam(defaultValue = "password123") String password) {
        String hash = passwordEncoder.encode(password);
        boolean matches = passwordEncoder.matches(password, hash);
        
        Map<String, String> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", hash);
        result.put("verified", String.valueOf(matches));
        result.put("sql", "UPDATE users SET password = '" + hash + "' WHERE username = 'admin@academy.com';");
        
        log.info("Generated BCrypt hash for password: {}", password);
        return result;
    }
    
    @GetMapping("/verify-hash")
    public Map<String, Object> verifyHash(
            @RequestParam String password,
            @RequestParam String hash) {
        boolean matches = passwordEncoder.matches(password, hash);
        
        Map<String, Object> result = new HashMap<>();
        result.put("password", password);
        result.put("hash", hash);
        result.put("matches", matches);
        
        return result;
    }
}

