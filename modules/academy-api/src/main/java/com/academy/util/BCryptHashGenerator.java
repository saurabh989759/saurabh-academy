package com.academy.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Utility to generate BCrypt hash for password123
 * Run with: java -jar app.jar --spring.main.web-application-type=none
 * Or use this in a test/script
 */
@Component
public class BCryptHashGenerator implements CommandLineRunner {
    
    @Override
    public void run(String... args) {
        if (args.length > 0 && args[0].equals("generate-hash")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String password = "password123";
            String existingHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
            
            System.out.println("========================================");
            System.out.println("BCrypt Hash Generator");
            System.out.println("========================================");
            System.out.println("Password: " + password);
            System.out.println("Existing Hash: " + existingHash);
            
            boolean matches = encoder.matches(password, existingHash);
            System.out.println("Existing Hash Matches: " + matches);
            System.out.println("");
            
            if (!matches) {
                System.out.println("❌ Existing hash does NOT match!");
                System.out.println("Generating new hash...");
                String newHash = encoder.encode(password);
                System.out.println("New Hash: " + newHash);
                System.out.println("New Hash Verified: " + encoder.matches(password, newHash));
                System.out.println("");
                System.out.println("SQL Update Commands:");
                System.out.println("UPDATE users SET password = '" + newHash + "' WHERE username = 'admin@academy.com';");
                System.out.println("UPDATE users SET password = '" + newHash + "' WHERE username = 'user@academy.com';");
            } else {
                System.out.println("✅ Existing hash matches correctly!");
            }
            System.out.println("========================================");
        }
    }
}

