package com.weeding.time.app.service;

import com.weeding.time.app.builder.WeddingMapper;
import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.repository.WeddingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class WeddingService {
    private static final Logger logger = LoggerFactory.getLogger(WeddingService.class);

    @Autowired
    private WeddingRepository weddingRepository;

    @Autowired
    private WeddingMapper weddingMapper;

    public Optional<Wedding> findWeddingById(Long weddingId) {
        return weddingRepository.findById(weddingId);
    }

    public List<Wedding> findAllWeddings() {
        return weddingRepository.findAll();
    }

    public Wedding createWedding(WeddingDto weddingDto) {
        Wedding wedding = weddingMapper.toEntity(weddingDto);
        // Ustawianie dodatkowych pól, które nie pochodzą z DTO
        wedding.setAccessCode(generateAccessCode());
        wedding.setCreatedAt(LocalDateTime.now());
        return wedding;
    }

    private String generateAccessCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
    }

}
