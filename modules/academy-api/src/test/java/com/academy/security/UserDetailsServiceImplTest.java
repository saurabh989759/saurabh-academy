package com.academy.security;

import com.academy.entity.User;
import com.academy.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Comprehensive unit tests for UserDetailsServiceImpl
 * Covers all methods, branches, and edge cases for 100% coverage
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserDetailsServiceImpl Tests")
class UserDetailsServiceImplTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;
    
    private User userEntity;
    
    @BeforeEach
    void setUp() {
        userEntity = new User();
        userEntity.setId(1L);
        userEntity.setUsername("test@example.com");
        userEntity.setPassword("$2a$10$hashedPassword");
        userEntity.setRole("ROLE_USER");
        userEntity.setEnabled(true);
    }
    
    @Test
    @DisplayName("Should load user by username successfully")
    void loadUserByUsername_WhenExists_ReturnsUserDetails() {
        // Given
        String username = "test@example.com";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(username);
        assertThat(userDetails.getPassword()).isEqualTo(userEntity.getPassword());
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonExpired()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.isCredentialsNonExpired()).isTrue();
        verify(userRepository).findByUsername(username);
    }
    
    @Test
    @DisplayName("Should throw UsernameNotFoundException when user not found")
    void loadUserByUsername_WhenNotFound_ThrowsException() {
        // Given
        String username = "notfound@example.com";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
        
        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("notfound@example.com");
        verify(userRepository).findByUsername(username);
    }
    
    @Test
    @DisplayName("Should handle disabled user")
    void loadUserByUsername_WhenUserDisabled_ThrowsException() {
        // Given
        String username = "test@example.com";
        userEntity.setEnabled(false);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        
        // When/Then
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername(username))
            .isInstanceOf(UsernameNotFoundException.class)
            .hasMessageContaining("disabled");
    }
    
    @Test
    @DisplayName("Should handle different roles")
    void loadUserByUsername_WithDifferentRole_ReturnsCorrectRole() {
        // Given
        String username = "admin@example.com";
        userEntity.setRole("ROLE_ADMIN");
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userEntity));
        
        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Then
        assertThat(userDetails.getAuthorities()).hasSize(1);
        assertThat(userDetails.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }
}

