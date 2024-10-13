package com.weeding.time.app.service;

import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.repository.ApplicationUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService {

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private JWTService jwtService;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public ApplicationUser register(ApplicationUser applicationUser) {
        applicationUser.setEncryptedPassword(encoder.encode(applicationUser.getEncryptedPassword()));
        return applicationUserRepository.save(applicationUser);
    }


    public String verify(ApplicationUser applicationUser) {
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(applicationUser.getFirstName(), applicationUser.getEncryptedPassword()));

        if (authentication.isAuthenticated()) {
            return jwtService.generateToken(applicationUser.getFirstName());
        }
        return "fail";
    }

}
