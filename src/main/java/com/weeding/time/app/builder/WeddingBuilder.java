package com.weeding.time.app.builder;

import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.Wedding;
import org.springframework.stereotype.Component;

@Component
public class WeddingBuilder {

    // Budowanie DTO z encji Wedding
    public WeddingDto buildDto(Wedding wedding) {
        if (wedding == null) {
            throw new IllegalArgumentException("Wedding cannot be null");
        }

        return WeddingDto.builder()
                .weddingId(wedding.getWeddingId())
                .weddingName(wedding.getWeddingName())
                .weddingDate(wedding.getWeddingDate())
                .location(wedding.getLocation())
                .accessCode(wedding.getAccessCode())
                .createdAt(wedding.getCreatedAt())
                .build();
    }

    // Konwersja z DTO WeddingDto do encji Wedding
    public Wedding buildDomain(WeddingDto weddingDto) {
        if (weddingDto == null) {
            throw new IllegalArgumentException("WeddingDTO cannot be null");
        }

        return Wedding.builder()
                .weddingId(weddingDto.getWeddingId())
                .weddingName(weddingDto.getWeddingName())
                .weddingDate(weddingDto.getWeddingDate())
                .location(weddingDto.getLocation())
                .accessCode(weddingDto.getAccessCode())
                .createdAt(weddingDto.getCreatedAt())
                .build();
    }
}
