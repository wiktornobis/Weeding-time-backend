package com.weeding.time.app.service;

import com.weeding.time.app.builder.WeddingBuilder;
import com.weeding.time.app.dto.WeddingDto;
import com.weeding.time.app.model.Wedding;
import com.weeding.time.app.repository.WeddingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class WeddingService {

    @Autowired
    private WeddingRepository weddingRepository;

    @Autowired
    private WeddingBuilder weddingBuilder;  // Wstrzykujemy WeddingBuilder do serwisu, aby konwertować do DTO

    // Sprawdzanie, czy kod dostępu jest poprawny
    public boolean isValidAccessCode(String accessCode) {
        return weddingRepository.existsByAccessCode(accessCode);
    }

    // Zwracanie wesela po jego ID (model Wedding)
    public Optional<Wedding> findWeddingById(Long weddingId) {
        return weddingRepository.findById(weddingId);
    }

    // Zwracanie wszystkich wesel (model Wedding)
    public List<Wedding> findAllWeddings() {
        return weddingRepository.findAll();
    }

    // Tworzenie nowego wesela
    public Wedding createWedding(Wedding wedding) {
        // Walidacja unikalności kodu dostępu
        if (weddingRepository.existsByAccessCode(wedding.getAccessCode())) {
            throw new IllegalArgumentException("Access code must be unique");
        }
        return weddingRepository.save(wedding);
    }

    // Zapisywanie wesela (model Wedding)
    public Wedding saveWedding(Wedding wedding) {
        return weddingRepository.save(wedding);
    }

    // Aktualizacja istniejącego wesela
    public Wedding updateWedding(Long weddingId, Wedding updatedWedding) {
        if (!weddingRepository.existsById(weddingId)) {
            throw new IllegalArgumentException("Wedding with the given ID does not exist");
        }
        updatedWedding.setWeddingId(weddingId);
        return weddingRepository.save(updatedWedding);
    }

    // Usunięcie wesela
    public void deleteWedding(Long weddingId) {
        if (!weddingRepository.existsById(weddingId)) {
            throw new IllegalArgumentException("Wedding with the given ID does not exist");
        }
        weddingRepository.deleteById(weddingId);
    }

    // Metoda konwertująca Wedding na WeddingDto
    public WeddingDto convertToDto(Wedding wedding) {
        return weddingBuilder.buildDto(wedding); // Zbudowanie WeddingDto za pomocą WeddingBuilder
    }

    // Metoda konwertująca listę Wedding na listę WeddingDto
    public List<WeddingDto> convertToDtoList(List<Wedding> weddings) {
        return weddings.stream()
                .map(weddingBuilder::buildDto)  // Dla każdego Wedding wywołujemy buildDto
                .toList();
    }
}
