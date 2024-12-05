package com.weeding.time.app.repository;

import com.weeding.time.app.model.Wedding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface WeddingRepository extends JpaRepository<Wedding, Long> {
    boolean existsByAccessCode(String accessCode);
    @Query("SELECT w FROM Wedding w WHERE w.accessCode = :accessCode")
    Optional<Wedding> findByAccessCode(String accessCode);
}
