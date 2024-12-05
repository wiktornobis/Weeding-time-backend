package com.weeding.time.app.service;

import com.weeding.time.app.builder.ApplicationUserMapper;
import com.weeding.time.app.builder.WeddingMapper;
import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.model.UserPrincipal;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.repository.ApplicationUserRepository;
import com.weeding.time.app.repository.WeddingRepository;
import com.weeding.time.app.types.ApplicationUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
    private WeddingMapper weddingMapper;

    @Autowired
    private ApplicationUserMapper applicationUserMapper;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    @Transactional
    public ApplicationUserDto registerUser(ApplicationUserDto applicationUserDto) {
        ApplicationUser applicationUser = applicationUserMapper.toEntity(applicationUserDto);

        Wedding wedding = null;
        ApplicationUserRole role = ApplicationUserRole.fromDisplayName(applicationUserDto.getRole());

        if (Arrays.asList(ApplicationUserRole.BRIDE, ApplicationUserRole.GROOM).contains(role)) {
            if (applicationUserDto.getWeeding() == null) {
                WeddingDto weddingDto = new WeddingDto();
                weddingDto.setWeddingDate(applicationUserDto.getWeddingDate());
                wedding = weddingService.createWedding(weddingDto);
            } else {
                wedding = weddingRepository.findById(Long.valueOf(applicationUserDto.getAccessCode()))
                        .orElseThrow(() -> new IllegalArgumentException("Wesele nie znalezione"));
            }
        } else if (role == ApplicationUserRole.GUEST) {
            String accessCode = applicationUserDto.getAccessCode();
            wedding = weddingRepository.findByAccessCode(accessCode)
                    .orElseThrow(() -> new IllegalArgumentException("Kod dostępu nie pasuje do żadnego wesela"));
        } else {
            throw new IllegalArgumentException("Nieznana rola");
        }

        // Przypisanie wesela do użytkownika
        applicationUser.setWedding(wedding);
        applicationUser.setEncryptedPassword(encoder.encode(applicationUserDto.getEncryptedPassword()));

        ApplicationUser savedUser = applicationUserRepository.save(applicationUser);
        return applicationUserMapper.toDto(savedUser);
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
