package com.weeding.time.app.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ApplicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 128, name="first_name")
    private String firstName;
    @Column(length = 128, name="last_name")
    private String lastName;
    @Column(length = 64, name="encrypted_password")
    private String encryptedPassword;
    private String email;
    @Column(length = 20, name="phone_number")
    private String phoneNumber;
    @Column(length = 40, name="role")
    private String role;
    // Relacja wiele-do-jednego z Wedding
    @ManyToOne(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinColumn(name = "wedding_id", referencedColumnName = "wedding_id")
    private Wedding wedding;
}
