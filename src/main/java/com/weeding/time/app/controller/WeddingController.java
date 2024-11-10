package com.weeding.time.app.controller;

import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.service.WeddingService;
import com.weeding.time.app.builder.WeddingBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/weddings")
public class WeddingController {

    @Autowired
    private WeddingService weddingService;

    @Autowired
    private WeddingBuilder weddingBuilder;

    // Przykładowa metoda zwracająca WeddingDTO
    @GetMapping("/{id}")
    public WeddingDto getWeddingById(@PathVariable Long id) {
        return weddingService.findWeddingById(id)
                .map(weddingBuilder::buildDto)  // If present, map to DTO
                .orElseThrow(() -> new IllegalArgumentException("Wedding not found"));
    }

    // Przykładowa metoda zwracająca listę WeddingDTO
    @GetMapping
    public List<WeddingDto> getAllWeddings() {
        List<Wedding> weddings = weddingService.findAllWeddings();
        return weddings.stream()
                .map(weddingBuilder::buildDto) // Użycie buildera do konwersji na DTO
                .toList();
    }

    // Przykładowa metoda tworzenia nowego wesela
    @PostMapping
    public WeddingDto createWedding(@RequestBody WeddingDto weddingDTO) {
        Wedding wedding = weddingBuilder.buildDomain(weddingDTO); // Przekształcenie DTO na encję
        Wedding createdWedding = weddingService.saveWedding(wedding);
        return weddingBuilder.buildDto(createdWedding);
    }
}
