package com.weeding.time.app.service;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.model.UserPrincipal;
import com.weeding.time.app.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public ApplicationUser register(ApplicationUser applicationUser) {
        applicationUser.setEncryptedPassword(encoder.encode(applicationUser.getEncryptedPassword()));
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


    public String refreshAccessToken(String refreshToken, String email) {
        ApplicationUser applicationUser = applicationUserRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Użytkownik nie znaleziony"));

        UserDetails userDetails = new UserPrincipal(applicationUser);
        return jwtService.refreshAccessToken(refreshToken, userDetails);
    }
}
