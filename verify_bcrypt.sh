#!/bin/bash
# Script to verify and regenerate BCrypt hash

echo "🔍 Verifying BCrypt Hash..."
echo ""

# Create a simple Java program to test BCrypt
cat > /tmp/BcryptTest.java << 'JAVAEOF'
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BcryptTest {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String existingHash = "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy";
        
        System.out.println("Testing BCrypt hash verification...");
        System.out.println("Password: " + password);
        System.out.println("Existing Hash: " + existingHash);
        System.out.println("");
        
        boolean matches = encoder.matches(password, existingHash);
        System.out.println("✅ Hash matches: " + matches);
        
        if (!matches) {
            System.out.println("");
            System.out.println("❌ Hash does NOT match! Generating new hash...");
            String newHash = encoder.encode(password);
            System.out.println("New Hash: " + newHash);
            System.out.println("New Hash Verified: " + encoder.matches(password, newHash));
        }
    }
}
JAVAEOF

echo "Java test file created. Now let's check if we can run it via Gradle..."

