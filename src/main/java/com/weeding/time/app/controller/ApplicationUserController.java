package com.weeding.time.app.controller;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.service.ApplicationUserService;
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
    @CrossOrigin(origins = "http://localhost:8099")
    public ResponseEntity<Map<String, String>> login(@RequestBody ApplicationUser applicationUser) {
        Map<String, String> tokens = applicationUserService.verify(applicationUser);
        if (tokens != null) {
            return ResponseEntity.ok(tokens);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        String email = request.get("email"); // Zakładam, że email jest przesyłany w żądaniu

        String newAccessToken = applicationUserService.refreshAccessToken(refreshToken, email);
        Map<String, String> response = new HashMap<>();
        response.put("accessToken", newAccessToken);

        return ResponseEntity.ok(response);
    }
}
