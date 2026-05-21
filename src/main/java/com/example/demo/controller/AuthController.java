package com.example.demo.controller;

import com.example.demo.dto.AuthRequestDTO;
import com.example.demo.dto.AuthResponseDTO;
import com.example.demo.dto.RefreshRequestDTO;
import com.example.demo.dto.UserRequestDTO;
import com.example.demo.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UserRequestDTO request) {
        AuthResponseDTO response = authService.register(request);
        return withTokenCookies(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody AuthRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return withTokenCookies(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(
            @RequestBody(required = false) RefreshRequestDTO request,
            HttpServletRequest httpRequest
    ) {
        String refreshToken = getCookieValue(httpRequest, "refreshToken");
        if ((refreshToken == null || refreshToken.isBlank()) && request != null) {
            refreshToken = request.getRefreshToken();
        }
        AuthResponseDTO response = authService.refresh(refreshToken);
        return withTokenCookies(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        authService.logout(authentication);
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie("accessToken").toString())
                .header(HttpHeaders.SET_COOKIE, clearCookie("refreshToken").toString())
                .body("Logged out successfully");
    }

    private ResponseEntity<AuthResponseDTO> withTokenCookies(AuthResponseDTO response) {
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, tokenCookie("accessToken", response.getAccessToken(), 15 * 60).toString())
                .header(HttpHeaders.SET_COOKIE, tokenCookie("refreshToken", response.getRefreshToken(), 7 * 24 * 60 * 60).toString())
                .body(response);
    }

    private ResponseCookie tokenCookie(String name, String token, long maxAgeSeconds) {
        return ResponseCookie.from(name, token)
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAgeSeconds)
                .build();
    }

    private ResponseCookie clearCookie(String name) {
        return ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
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
}
