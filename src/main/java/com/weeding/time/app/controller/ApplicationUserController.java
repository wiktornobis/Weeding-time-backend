package com.weeding.time.app.controller;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.service.ApplicationUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApplicationUserController {

    @Autowired
    private ApplicationUserService applicationUserService;
    @PostMapping("/register")
    public ResponseEntity<ApplicationUser> register(@RequestBody ApplicationUser applicationUser) {
        // Register the user and return the result
        ApplicationUser registeredUser = applicationUserService.register(applicationUser);

        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public String login(@RequestBody ApplicationUser applicationUser) {
        return applicationUserService.verify(applicationUser);
    }

}
