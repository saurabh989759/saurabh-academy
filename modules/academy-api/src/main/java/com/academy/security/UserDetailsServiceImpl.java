package com.academy.security;

import com.academy.entity.User;
import com.academy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Looking up user: {}", username);

        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                log.warn("No account found for: {}", username);
                return new UsernameNotFoundException("No account found for: " + username);
            });

        if (!user.getEnabled()) {
            log.warn("Account disabled for: {}", username);
            throw new UsernameNotFoundException("Account is disabled: " + username);
        }

        log.debug("Account resolved: {} role={}", username, user.getRole());

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(List.of(new SimpleGrantedAuthority(user.getRole())))
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!user.getEnabled())
            .build();
    }
}
