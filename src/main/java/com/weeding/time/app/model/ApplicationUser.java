package com.weeding.time.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ApplicationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 128, name = "first_name")
    private String firstName;

    @Column(length = 128, name = "last_name")
    private String lastName;

    @Column(length = 64, name = "encrypted_password")
    private String encryptedPassword;

    @Column(name = "email")
    private String email;

    @Column(length = 20, name = "phone_number")
    private String phoneNumber;

    @Column(length = 40, name = "role")
    private String role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ApplicationUserWedding> userWeddings = new HashSet<>();
}
