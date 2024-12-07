package com.weeding.time.app.controller;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.service.ApplicationUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApplicationUserController {

    @Autowired
    private ApplicationUserService applicationUserService;
    @PostMapping("/refresh-token")
    @CrossOrigin(origins = "http://localhost:8099", allowCredentials = "true")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody Map<String, String> requestBody, HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        String email = requestBody.get("email");

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Brak refresh tokena"));
        }

        Map<String, String> newTokens = applicationUserService.refreshAccessToken(refreshToken, email);

        if (newTokens == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Odświeżenie tokena nie powiodło się"));
        }

        Cookie accessCookie = new Cookie("accessToken", newTokens.get("accessToken"));
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(60 * 60); // 60 minut

        response.addCookie(accessCookie);

        return ResponseEntity.ok(Map.of("accessToken", newTokens.get("accessToken")));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie refreshCookie = new Cookie("refreshToken", null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(0);

        Cookie accessCookie = new Cookie("accessToken", null);
        accessCookie.setHttpOnly(true);
        accessCookie.setSecure(true);
        accessCookie.setPath("/");
        accessCookie.setMaxAge(0);

        response.addCookie(refreshCookie);
        response.addCookie(accessCookie);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-auth")
    @CrossOrigin(origins = "http://localhost:8099", allowCredentials = "true")
    public ResponseEntity<Map<String, Object>> checkAuth(HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> responseAuth = new HashMap<>();
        boolean isAuthenticated = false;
        String accessToken = null;
        String refreshToken = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                } else if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (accessToken != null && applicationUserService.isAccessTokenValid(accessToken)) {
            isAuthenticated = true;
        } else if (refreshToken != null && applicationUserService.isRefreshTokenValid(refreshToken)) {
            Map<String, String> newTokens = applicationUserService.refreshAccessToken(refreshToken, null);
            if (newTokens != null) {
                Cookie newAccessCookie = new Cookie("accessToken", newTokens.get("accessToken"));
                newAccessCookie.setHttpOnly(true);
                newAccessCookie.setSecure(true);
                newAccessCookie.setPath("/");
                newAccessCookie.setMaxAge(60 * 60);

                response.addCookie(newAccessCookie);
                isAuthenticated = true;
            }
        }

        responseAuth.put("userAuth", isAuthenticated);
        responseAuth.put("userRole", "ADMIN");
        // TODO: Mapowanie roli użytkownika z bazy danych.

        return ResponseEntity.ok(responseAuth);
    }
}
