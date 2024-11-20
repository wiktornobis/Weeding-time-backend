package com.weeding.time.app.service;

import com.weeding.time.app.builder.ApplicationUserMapper;
import com.weeding.time.app.dto.ApplicationUserDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private WeddingRepository weddingRepository;
    @Autowired
    private WeddingService weddingService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationUserMapper applicationUserMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public ApplicationUserDto registerUser(ApplicationUserDto applicationUserDto) {
        validateUserRole(applicationUserDto);

        // Mapowanie DTO na encję
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDto);

        // Tworzenie wesela, jeśli dotyczy
        Wedding wedding = applicationUserDto.getWeeding() != null
                ? applicationUserDto.getWeeding()
                : createWedding(applicationUserDto);

        Wedding savedWedding = weddingRepository.save(wedding);
        applicationUser.setWedding(savedWedding);

        // Szyfrowanie hasła
        applicationUser.setEncryptedPassword(encoder.encode(applicationUserDto.getEncryptedPassword()));

        // Zapis użytkownika w bazie
        ApplicationUser savedUser = applicationUserRepository.save(applicationUser);

        return applicationUserMapper.toDto(savedUser);
    }

    private void validateUserRole(ApplicationUserDto applicationUserDto) {
        String role = applicationUserDto.getRole();
        String accessCode = applicationUserDto.getWeeding() != null
                ? applicationUserDto.getWeeding().getAccessCode()
                : null;

        switch (role) {
            case "Pan Młody":
            case "Panna Młoda":
                if (applicationUserDto.getWeeding() == null) {
                    applicationUserDto.setWeeding(createWedding(applicationUserDto));
                }
                break;
            case "Gość":
            case "Świadek":
                if (accessCode == null || !weddingService.isValidAccessCode(accessCode)) {
                    throw new IllegalArgumentException("Invalid access code for role: " + role);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown user role: " + role);
        }
    }

    private String generateWeddingName(ApplicationUserDto applicationUserDto) {
        return switch (applicationUserDto.getRole()) {
            case "Panna Młoda" -> applicationUserDto.getFirstName() + "'s Wedding";
            case "Pan Młody" -> "Wedding of " + applicationUserDto.getFirstName();
            default -> "Wesele";
        };
    }

    private Wedding createWedding(ApplicationUserDto applicationUserDto) {
        String weddingName = generateWeddingName(applicationUserDto);
        return Wedding.builder()
                .weddingName(weddingName)
                .weddingDate(LocalDate.now().plusMonths(6)) // Przykładowa data
                .location("Default Location")
                .accessCode(UUID.randomUUID().toString().replace("-", "").substring(0, 15))
                .build();
    }

    public Map<String, String> verify(ApplicationUserDto applicationUserDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        applicationUserDto.getEmail(),
                        applicationUserDto.getEncryptedPassword()
                )
        );

        if (!authentication.isAuthenticated()) {
            throw new RuntimeException("Uwierzytelnienie nie powiodło się.");
        }

        ApplicationUser user = applicationUserRepository.findByEmail(applicationUserDto.getEmail())
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        return jwtService.generateTokens(user.getEmail(), user.getRole());
    }

    public Map<String, String> refreshAccessToken(String refreshToken, String email) {
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        UserDetails userDetails = new UserPrincipal(applicationUser);
        String newAccessToken = jwtService.refreshAccessToken(refreshToken, userDetails);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", newAccessToken);
        return tokens;
    }

    public boolean isAccessTokenValid(String accessToken) {
        return jwtService.isAccessTokenValid(accessToken);
    }

    public boolean isRefreshTokenValid(String refreshToken) {
        return jwtService.isRefreshTokenValid(refreshToken);
    }
}
