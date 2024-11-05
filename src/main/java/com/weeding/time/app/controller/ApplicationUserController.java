package com.weeding.time.app.controller;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.service.ApplicationUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
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

    @PostMapping("/register")
    public ResponseEntity<ApplicationUser> register(@RequestBody ApplicationUser applicationUser) {
        ApplicationUser registeredUser = applicationUserService.register(applicationUser);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    @CrossOrigin(origins = "http://localhost:8099", allowCredentials = "true")
    public ResponseEntity<Map<String, Object>> login(@RequestBody ApplicationUser applicationUser, HttpServletResponse response) {
        Map<String, String> tokens = applicationUserService.verify(applicationUser);
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
            // W przypadku błędu zwróć treść odpowiedzi z informacją o błędnym logowaniu
            responseMap.put("message", "Błędny login lub hasło"); // Dodatkowy komunikat
            responseMap.put("userIsAuth", false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseMap);  // Użycie 401 Unauthorized i treści błędu
        }
    }


    @PostMapping("/refresh-token")
    @CrossOrigin(origins = "http://localhost:8099", allowCredentials = "true")
    public ResponseEntity<Map<String, String>> refreshToken(
            @RequestBody Map<String, String> requestBody, HttpServletRequest request, HttpServletResponse response) {

        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("refreshToken")) {
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

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("accessToken", newTokens.get("accessToken"));
        return ResponseEntity.ok(responseMap);
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
                if (cookie.getName().equals("accessToken")) {
                    accessToken = cookie.getValue();
                } else if (cookie.getName().equals("refreshToken")) {
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
//      //TODO: dorobić przekazywanie user role z bazy danych

        return ResponseEntity.ok(responseAuth);
    }
}
