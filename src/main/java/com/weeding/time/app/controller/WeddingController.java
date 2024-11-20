package com.weeding.time.app.controller;

import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.service.WeddingService;
import com.weeding.time.app.builder.WeddingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/weddings")
public class WeddingController {

    @Autowired
    private WeddingService weddingService;

    @Autowired
    private WeddingMapper weddingMapper;

    @GetMapping("/{id}")
    public WeddingDto getWeddingById(@PathVariable Long id) {
        return weddingService.findWeddingById(id)
                .map(weddingMapper::toDto)  // Mapowanie z Wedding na WeddingDto
                .orElseThrow(() -> new IllegalArgumentException("Wedding not found"));
    }

    @GetMapping
    public List<WeddingDto> getAllWeddings() {
        List<Wedding> weddings = weddingService.findAllWeddings();
        return weddings.stream()
                .map(weddingMapper::toDto) // Mapowanie każdego Wedding na WeddingDto
                .collect(Collectors.toList());  // Użycie Collectors.toList() dla kompatybilności z Java 11 i wcześniejszymi
    }

    @PostMapping
    public WeddingDto createWedding(@RequestBody WeddingDto weddingDto) {
        Wedding wedding = weddingMapper.toEntity(weddingDto); // Przekształcenie WeddingDto na Wedding (encję)
        Wedding createdWedding = weddingService.saveWedding(wedding);  // Zapisanie wesela
        return weddingMapper.toDto(createdWedding);  // Zwrócenie WeddingDto
    }
}
