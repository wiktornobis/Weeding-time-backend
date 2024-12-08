package com.weeding.time.app.dto;

import com.weeding.time.app.model.Wedding;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApplicationUserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String encryptedPassword;
    private String role;
    private String email;
    private String phoneNumber;
    private LocalDate weddingDate;
    private String accessCode;
    private Wedding wedding;
}
