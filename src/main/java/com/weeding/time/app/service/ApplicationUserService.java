package com.weeding.time.app.service;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.model.UserPrincipal;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.repository.ApplicationUserRepository;
import com.weeding.time.app.repository.WeddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired WeddingRepository weddingRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public ApplicationUser registerUser(ApplicationUserDto applicationUserDto) {
        // 1. Tworzymy nowe wesele
        Wedding wedding = new Wedding();
        wedding.setWeddingName(applicationUserDto.getFirstName() + " " + applicationUserDto.getLastName() + " Wedding");
        wedding.setWeddingDate(applicationUserDto.getWeeding().getWeddingDate());
        wedding.setLocation(null); // Przykładowa lokalizacja
        wedding.setCreatedAt(java.time.LocalDateTime.now()); // Ustawiamy datę utworzenia

        // Generowanie accessCode dla "Pan Młody" i "Panna Młoda"
        if (applicationUserDto.getRole().equals("Panna Młoda") || applicationUserDto.getRole().equals("Pan Młody")) {
            wedding.setAccessCode(UUID.randomUUID().toString());  // Generowanie unikalnego accessCode
        } else {
            wedding.setAccessCode(applicationUserDto.getWeeding().getAccessCode()); // Używamy podanego accessCode
        }

        // 2. Zapisujemy wesele
        Wedding savedWedding = weddingRepository.save(wedding);

        // 3. Tworzymy użytkownika
        ApplicationUser applicationUser = new ApplicationUser();
        applicationUser.setFirstName(applicationUserDto.getFirstName());
        applicationUser.setLastName(applicationUserDto.getLastName());
        applicationUser.setEmail(applicationUserDto.getEmail());
        applicationUser.setPhoneNumber(applicationUserDto.getPhoneNumber());
        applicationUser.setRole(applicationUserDto.getRole());
        applicationUser.setEncryptedPassword(encoder.encode(applicationUserDto.getEncryptedPassword())); // Szyfrowanie hasła
        applicationUser.setWedding(savedWedding); // Przypisujemy wesele do użytkownika

        // 4. Zapisujemy użytkownika
        return applicationUserRepository.save(applicationUser);
    }



    public Map<String, String> verify(ApplicationUser applicationUser) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(applicationUser.getEmail(), applicationUser.getEncryptedPassword())
        );

        if (authentication.isAuthenticated()) {
            ApplicationUser user = applicationUserRepository.findByEmail(applicationUser.getEmail())
                    .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

            return jwtService.generateTokens(user.getEmail(), user.getRole());
        } else {
            throw new RuntimeException("Uwierzytelnienie nie powiodło się.");
        }
    }

    public Map<String, String> refreshAccessToken(String refreshToken, String email) {
        // Znalezienie użytkownika po adresie email
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        // Walidacja refresh tokena i generowanie nowego access tokena
        UserDetails userDetails = new UserPrincipal(applicationUser);
        String newAccessToken = jwtService.refreshAccessToken(refreshToken, userDetails);

        // Zwracamy nowy access token
        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        return tokens;
    }
    public boolean isAccessTokenValid(String accessToken) {
        return jwtService.isAccessTokenValid(accessToken);
    }

    // Nowa metoda do sprawdzenia poprawności refresh tokena
    public boolean isRefreshTokenValid(String refreshToken) {
        return jwtService.isRefreshTokenValid(refreshToken);
    }
}
