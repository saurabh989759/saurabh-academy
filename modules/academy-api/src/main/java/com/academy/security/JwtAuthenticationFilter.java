package com.academy.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Value("${jwt.header.name:Authorization}")
    private String headerName;

    @Value("${jwt.header.prefix:Bearer }")
    private String tokenPrefix;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain chain) throws ServletException, IOException {

        if (isAuthEndpoint(request)) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(headerName);
        if (header == null || !header.startsWith(tokenPrefix)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = header.substring(tokenPrefix.length());
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtService.validateToken(token)) {
                    var authToken = new UsernamePasswordAuthenticationToken(
                        username, null, resolveAuthorities(token)
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Authenticated request for user: {}", username);
                } else {
                    log.warn("Rejected token for user: {}", username);
                }
            }
        } catch (Exception ex) {
            log.error("Token processing error: {}", ex.getMessage());
        }

        chain.doFilter(request, response);
    }

    private boolean isAuthEndpoint(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/api/auth/");
    }

    private List<SimpleGrantedAuthority> resolveAuthorities(String token) {
        try {
            String roles = jwtService.extractClaim(token, claims -> {
                Object r = claims.get("roles");
                return r != null ? r.toString() : "ROLE_USER";
            });
            return Arrays.stream(roles.split(","))
                .map(r -> new SimpleGrantedAuthority(r.startsWith("ROLE_") ? r : "ROLE_" + r))
                .toList();
        } catch (Exception ex) {
            log.warn("Could not resolve authorities from token: {}", ex.getMessage());
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
    }
}
