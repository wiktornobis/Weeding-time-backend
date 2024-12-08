package com.weeding.time.app.service;

import com.weeding.time.app.builder.ApplicationUserMapper;
import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.model.UserPrincipal;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.repository.ApplicationUserRepository;
import com.weeding.time.app.repository.WeddingRepository;
import com.weeding.time.app.types.ApplicationUserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ApplicationUserService {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationUserService.class);

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private ApplicationUserWeddingService userWeddingService;

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

    public ApplicationUserDto registerUser(ApplicationUserDto applicationUserDto) {
        String email = applicationUserDto.getEmail();

        // Sprawdzanie, czy użytkownik z takim adresem e-mail już istnieje
        Optional<ApplicationUser> existingUser = applicationUserRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            logger.warn("Attempt to register with an already taken email: {}", email);
            throw new IllegalArgumentException("Podany adres e-mail jest już zajęty.");
        }

        // Mapowanie DTO na encję użytkownika
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDto);
        applicationUser.setEncryptedPassword(encoder.encode(applicationUserDto.getEncryptedPassword()));

        Wedding wedding = null;
        ApplicationUserRole role = ApplicationUserRole.displayRoleName(applicationUserDto.getRole());

        try {
            if (Arrays.asList(ApplicationUserRole.BRIDE, ApplicationUserRole.GROOM).contains(role)) {
                if (applicationUserDto.getWedding() == null) {
                    WeddingDto weddingDto = new WeddingDto();
                    weddingDto.setWeddingDate(applicationUserDto.getWeddingDate());
                    wedding = weddingService.createWedding(weddingDto);
                    wedding = weddingRepository.save(wedding);
                    logger.info("Utworzono nowe wesele o id: {}", wedding.getId());
                }
            } else if (Arrays.asList(ApplicationUserRole.GUEST, ApplicationUserRole.WITNESS).contains(role)) {
                String accessCode = applicationUserDto.getAccessCode();
                wedding = weddingRepository.findByAccessCode(accessCode)
                        .orElseThrow(() -> new IllegalArgumentException("Kod dostępu nie pasuje do żadnego wesela"));
            } else {
                throw new IllegalArgumentException("Nieznana rola");
            }

            // Zapisanie użytkownika do bazy danych
            ApplicationUser savedUser = applicationUserRepository.save(applicationUser);
            logger.info("Pomyślnie zarejestrowano użytkownika: {}", savedUser.getEmail());

            userWeddingService.createUserWeddingRelation(savedUser, wedding);

            return applicationUserMapper.toDto(savedUser);

        } catch (Exception exception) {
            logger.error("Błąd podczas rejestracji: {}", exception.getMessage());
            throw exception;
        }
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
