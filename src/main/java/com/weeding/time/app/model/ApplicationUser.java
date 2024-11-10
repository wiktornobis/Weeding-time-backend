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
    private String firstName;
    private String lastName;
    private String encryptedPassword;
    private String role;
    private String email;
    private String phoneNumber;
    private Date weedingDate;
    // Relacja wiele-do-jednego z Wedding
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "wedding_id", referencedColumnName = "wedding_id")
    private Wedding wedding;
}
