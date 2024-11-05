package com.weeding.time.app.builder;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.model.ApplicationUser;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ApplicationUserBuilder  {


    public ApplicationUserDto buildDto(ApplicationUser applicationUser) {
        if (applicationUser == null) {
            throw new IllegalArgumentException("ApplicationUser cannot be null");
        }

        // Używamy Buildera dla DTO
        return ApplicationUserDto.builder()
                .id(applicationUser.getId())
                .firstName(applicationUser.getFirstName())
                .lastName(applicationUser.getLastName())
                .encryptedPassword(applicationUser.getEncryptedPassword())
                .role(applicationUser.getRole())
                .email(applicationUser.getEmail())
                .phoneNumber(applicationUser.getPhoneNumber())
                .weedingDate(applicationUser.getWeedingDate())
                .build();
    }

    public ApplicationUser buildDomain(ApplicationUserDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("ApplicationUserDto cannot be null");
        }

        return ApplicationUser.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .email(dto.getEmail())
                .phoneNumber(dto.getPhoneNumber())
                .weedingDate(dto.getWeedingDate())
                .build();
    }
}
