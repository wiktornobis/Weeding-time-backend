package com.weeding.time.app.builder;

import com.weeding.time.app.dto.ApplicationUserDto;
import com.weeding.time.app.model.ApplicationUser;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserMapper {

    public ApplicationUserDto toDto(@NonNull ApplicationUser applicationUser) {
        return ApplicationUserDto.builder()
                .id(applicationUser.getId())
                .firstName(applicationUser.getFirstName())
                .lastName(applicationUser.getLastName())
                .encryptedPassword(applicationUser.getEncryptedPassword())
                .role(applicationUser.getRole())
                .email(applicationUser.getEmail() != null ? applicationUser.getEmail().toLowerCase() : null)
                .phoneNumber(applicationUser.getPhoneNumber())
                .weeding(applicationUser.getWedding())
                .build();
    }

    public ApplicationUser toEntity(@NonNull ApplicationUserDto dto) {
        return ApplicationUser.builder()
                .id(dto.getId())
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .role(dto.getRole())
                .email(dto.getEmail() != null ? dto.getEmail().toLowerCase() : null)
                .phoneNumber(dto.getPhoneNumber())
                .wedding(dto.getWeeding())
                .build();
    }
}
