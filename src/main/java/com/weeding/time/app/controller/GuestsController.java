package com.weeding.time.app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GuestsController {

    @GetMapping("/api/guests")
    public String Hello() {
        return "Welcome Guests ";
    }
}
