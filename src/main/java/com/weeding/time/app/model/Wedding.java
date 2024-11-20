package com.weeding.time.app.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wedding")
public class Wedding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "wedding_id")
    private Long weddingId;

    @Column(name = "wedding_name", nullable = false, length = 100)
    private String weddingName;

    @Column(name = "wedding_date")
    private LocalDate weddingDate;

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "access_code", unique = true, length = 20)
    private String accessCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Relacja wiele-do-jednego - jedno wesele ma wielu użytkowników
    @OneToMany(mappedBy = "wedding", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<ApplicationUser> users;

}
