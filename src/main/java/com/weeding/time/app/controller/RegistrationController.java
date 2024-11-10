package com.weeding.time.app.controller;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.service.ApplicationUserService;
import com.weeding.time.app.service.WeddingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "http://localhost:8099")
public class RegistrationController {

    @Autowired
    private ApplicationUserService applicationUserService;

    @Autowired
    private WeddingService weddingService;

    // Endpoint do rejestracji użytkownika
    @PostMapping
    public ResponseEntity<?> register(@RequestBody ApplicationUserDto applicationUserDto) {
        // Sprawdzanie roli użytkownika
        String role = applicationUserDto.getRole();
        String accessCode = applicationUserDto.getWeeding() != null ? applicationUserDto.getWeeding().getAccessCode() : null;

        if ("Pan Młody".equals(role) || "Panna Młoda".equals(role)) {
            // Generowanie unikalnego kodu dostępu dla ról "Pan Młody" i "Panna Młoda"
            applicationUserDto.getWeeding().setAccessCode(UUID.randomUUID().toString());
        } else if ("Gość".equals(role) || "Świadek".equals(role)) {
            // Weryfikacja kodu dostępu dla ról "Gość" i "Świadek"
            if (accessCode == null || !weddingService.isValidAccessCode(accessCode)) {
                ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
                problemDetail.setDetail("Invalid access code");
                problemDetail.setTitle("Access code validation failed");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
            }
        } else {
            // Obsługa nieznanej roli użytkownika
            ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
            problemDetail.setDetail("Unknown user role");
            problemDetail.setTitle("Role validation failed");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
        }

        // Rejestracja użytkownika
        try {
            ApplicationUser registeredUser = applicationUserService.registerUser(applicationUserDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (Exception e) {
            ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            problemDetail.setDetail("Error registering user: " + e.getMessage());
            problemDetail.setTitle("User registration failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
        }
    }
}
