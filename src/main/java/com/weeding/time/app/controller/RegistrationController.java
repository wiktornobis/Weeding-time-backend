package com.weeding.time.app.controller;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "http://localhost:8099")
public class RegistrationController {

    @Autowired
    private ApplicationUserService applicationUserService;

    @PostMapping
    public ResponseEntity<ApplicationUserDto> register(@RequestBody ApplicationUserDto applicationUserDto) {
        // Rejestracja użytkownika
        ApplicationUserDto registeredUser = applicationUserService.registerUser(applicationUserDto);

        // Zwracanie zarejestrowanego użytkownika
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }
}
