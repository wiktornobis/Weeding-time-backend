package com.weeding.time.app.controller;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/register")
@CrossOrigin(origins = "http://localhost:8099")
public class RegistrationController {

    @Autowired
    private ApplicationUserService applicationUserService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody ApplicationUserDto applicationUserDto) {
        // Rejestracja użytkownika
        ApplicationUserDto registeredUser = applicationUserService.registerUser(applicationUserDto);

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("status", HttpStatus.CREATED);
        responseMap.put("user_id", registeredUser.getId());
        responseMap.put("email", registeredUser.getEmail());
        responseMap.put("role", registeredUser.getRole());
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMap);
    }

}
