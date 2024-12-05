package com.weeding.time.app.builder;

import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.Wedding;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class WeddingMapper {

    // Metoda konwertująca Wedding na WeddingDto
    public WeddingDto toDto(@NonNull Wedding wedding) {
        return WeddingDto.builder()
                .weddingId(wedding.getWeddingId())
                .weddingName(wedding.getWeddingName())
                .weddingDate(wedding.getWeddingDate())
                .location(wedding.getLocation())
                .accessCode(wedding.getAccessCode())
                .createdAt(wedding.getCreatedAt())
                .build();
    }

    // Metoda konwertująca WeddingDto na Wedding
    public Wedding toEntity(WeddingDto weddingDto) {
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
