package com.weeding.time.app.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
public class ApplicationUserDto {
    private Integer id;
    private String firstName;
    private String lastName;
    private String encryptedPassword;
    private String role;
    private String email;
    private String phoneNumber;
    private Date weedingDate;
}
