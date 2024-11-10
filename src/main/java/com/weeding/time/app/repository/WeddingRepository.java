package com.weeding.time.app.repository;

import com.weeding.time.app.model.Wedding;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeddingRepository extends JpaRepository<Wedding, Long> {
    // Metoda do sprawdzenia, czy kod dostępu istnieje
    boolean existsByAccessCode(String accessCode);
}
