package com.weeding.time.app.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeddingDto {
    private Long weddingId;
    private String weddingName;
    private LocalDate weddingDate;
    private String location;
    private String accessCode;
    private LocalDateTime createdAt;
    private Long applicationUserId;
}
