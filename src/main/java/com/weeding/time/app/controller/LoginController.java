package com.weeding.time.app.controller;

import com.weeding.time.app.dto.ApplicationUserDto;
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
public class LoginController {
    @Autowired
    private ApplicationUserService applicationUserService;

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:8099", allowCredentials = "true")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ApplicationUserDto applicationUserDto, HttpServletResponse response) {
        // Weryfikacja i generowanie tokenów, przekazanie DTO do serwisu
        Map<String, String> tokens = applicationUserService.verify(applicationUserDto); // Przekazujemy DTO

        Map<String, Object> responseMap = new HashMap<>();

        if (tokens != null) {
            // Ustawienie cookies z tokenami
            Cookie refreshCookie = new Cookie("refreshToken", tokens.get("refreshToken"));
            refreshCookie.setHttpOnly(true);
            refreshCookie.setSecure(true); // Ustaw na true, jeśli używasz HTTPS
            refreshCookie.setPath("/");
            refreshCookie.setMaxAge(1000 * 60 * 60 * 24 * 14); // 14 dni
            response.addCookie(refreshCookie);

            Cookie accessCookie = new Cookie("accessToken", tokens.get("accessToken"));
            accessCookie.setHttpOnly(true);
            accessCookie.setSecure(true);
            accessCookie.setPath("/");
            accessCookie.setMaxAge(60 * 60); // 60 minut
            response.addCookie(accessCookie);

            responseMap.put("message", "Zalogowano pomyślnie");
            responseMap.put("userIsAuth", true);
            responseMap.put("userRole", tokens.get("userRole"));
            return ResponseEntity.ok(responseMap);
        } else {
            // Jeśli weryfikacja się nie powiodła
            responseMap.put("message", "Błędny login lub hasło");
            responseMap.put("userIsAuth", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);
        }
    }
}
