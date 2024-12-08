package com.weeding.time.app.service;

import com.weeding.time.app.model.ApplicationUserWedding;
import com.weeding.time.app.repository.ApplicationUserWeddingRepository;
import com.weeding.time.app.model.ApplicationUser;
import com.weeding.time.app.model.Wedding;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ApplicationUserWeddingService {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationUserWeddingService.class);
    private final ApplicationUserWeddingRepository repository;

    public ApplicationUserWeddingService(ApplicationUserWeddingRepository repository) {
        this.repository = repository;
    }

    public void createUserWeddingRelation(ApplicationUser savedUser, Wedding wedding) {
        if (wedding != null) {
            ApplicationUserWedding userWedding = new ApplicationUserWedding();
            userWedding.setUser(savedUser);
            userWedding.setWedding(wedding);
            userWedding.setJoinDate(LocalDateTime.now());

            repository.save(userWedding);
            logger.info("Użytkownik o id {} pomyślnie do wesela o id {}", savedUser.getId(), wedding.getId());
        }
    }
}
