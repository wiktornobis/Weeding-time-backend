package com.weeding.time.app.repository;

import com.weeding.time.app.model.ApplicationUserWedding;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ApplicationUserWeddingRepository extends JpaRepository<ApplicationUserWedding, Long> {
    List<ApplicationUserWedding> findByUser_Id(Long userId);

    List<ApplicationUserWedding> findByWeddingId(Long weddingId);

    boolean existsByUserIdAndWeddingId(Long userId, Long weddingId);
}

