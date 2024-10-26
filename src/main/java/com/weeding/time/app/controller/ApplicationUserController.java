package com.weeding.time.app.controller;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.service.ApplicationUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ApplicationUserController {

    @Autowired
    private ApplicationUserService applicationUserService;

    @PostMapping("/register")
    public ResponseEntity<ApplicationUser> register(@RequestBody ApplicationUser applicationUser) {
        // Rejestracja użytkownika i zwrócenie wyniku
        ApplicationUser registeredUser = applicationUserService.register(applicationUser);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:8099", allowCredentials = "true")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ApplicationUser applicationUser, HttpServletResponse response) {
        Map<String, String> tokens = applicationUserService.verify(applicationUser);

        // Przygotowanie odpowiedzi
        Map<String, Object> responseMap = new HashMap<>();

        if (tokens != null) {
            Cookie refreshCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true); // Ustawienie na true, jeśli używasz HTTPS
            refreshCookie.setPath("/"); // Ustawienie na ścieżkę, na której cookie ma być dostępne
                refreshCookie.setMaxAge(1000 * 60 * 60 * 24 * 14); // 14 days

            response.addCookie(refreshCookie); // Dodanie cookie do odpowiedzi
            // Odpowiedź, gdy jest autoryzacja
            responseMap.put("message", "Zalogowano pomyślnie");
            responseMap.put("userIsAuth", true);
            responseMap.put("role", tokens.get("role"));

        } else {
            responseMap.put("userIsAuth", false);
            responseMap.put("message", "Uwierzytelnienie nie powiodło się.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }

        return ResponseEntity.ok(responseMap);
    }


    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String email = request.get("email");

        String newAccessToken = applicationUserService.refreshAccessToken(refreshToken, email);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);

        response.addCookie(cookie);
        return ResponseEntity.ok().build();
    }
}
