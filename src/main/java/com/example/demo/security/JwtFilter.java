package com.example.demo.security;

import com.example.demo.model.RefreshToken;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RefreshTokenService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;

    public JwtFilter(
            JwtService jwtService,
            UserDetailsService userDetailsService,
            RefreshTokenService refreshTokenService,
            UserRepository userRepository
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String accessToken = null;
        String refreshToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            accessToken = authHeader.substring(7);
        } else {
            accessToken = getCookieValue(request, "accessToken");
        }
        refreshToken = getCookieValue(request, "refreshToken");

        if (accessToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtService.extractEmail(accessToken);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                if (jwtService.isTokenValid(accessToken, userDetails.getUsername())) {
                    setAuthentication(userDetails);
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (ExpiredJwtException ignored) {
                // continue with silent refresh
            } catch (Exception ex) {
                // continue with silent refresh path
            }
        }

        if (refreshToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                RefreshToken rotated = refreshTokenService.rotateRefreshToken(refreshToken);
                String email = rotated.getUser().getEmail();
                if (userRepository.findByEmail(email).isEmpty()) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                    return;
                }
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                String newAccessToken = jwtService.generateAccessToken(email);
                setAuthentication(userDetails);
                addTokenCookie(response, "accessToken", newAccessToken, 15 * 60);
                addTokenCookie(response, "refreshToken", rotated.getToken(), 7 * 24 * 60 * 60);
                filterChain.doFilter(request, response);
                return;
            } catch (RuntimeException ex) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                return;
            }
        }

        if (request.getRequestURI().startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }

    private void setAuthentication(UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private String getCookieValue(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void addTokenCookie(HttpServletResponse response, String name, String value, long maxAgeSeconds) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
