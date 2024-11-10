package com.weeding.time.app.service;

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

    public boolean isValidAccessCode(String accessCode) {
        // Sprawdzamy, czy kod dostępu istnieje w bazie danych wesela
        return weddingRepository.existsByAccessCode(accessCode);
    }

    // Metoda zwracająca wesele po jego ID
    public Optional<Wedding> findWeddingById(Long weddingId) {
        return weddingRepository.findById(weddingId);
    }

    // Metoda zwracająca wszystkie wesela
    public List<Wedding> findAllWeddings() {
        return weddingRepository.findAll();
    }

    // Metoda do tworzenia nowego wesela
    public Wedding createWedding(Wedding wedding) {
        // Można dodać logikę walidacji przed zapisaniem, np. sprawdzenie czy kod dostępu jest unikalny
        if (weddingRepository.existsByAccessCode(wedding.getAccessCode())) {
            throw new IllegalArgumentException("Access code must be unique");
        }
        return weddingRepository.save(wedding);
    }

    public Wedding saveWedding(Wedding wedding) {
        return weddingRepository.save(wedding); // Save wedding to the database
    }

    // Metoda do aktualizacji istniejącego wesela
    public Wedding updateWedding(Long weddingId, Wedding updatedWedding) {
        // Sprawdzamy, czy wesele o podanym ID istnieje
        if (!weddingRepository.existsById(weddingId)) {
            throw new IllegalArgumentException("Wedding with the given ID does not exist");
        }
        updatedWedding.setWeddingId(weddingId); // Ustawiamy ID, aby zachować poprawność encji
        return weddingRepository.save(updatedWedding);
    }

    // Metoda do usunięcia wesela
    public void deleteWedding(Long weddingId) {
        // Sprawdzamy, czy wesele o podanym ID istnieje
        if (!weddingRepository.existsById(weddingId)) {
            throw new IllegalArgumentException("Wedding with the given ID does not exist");
        }
        weddingRepository.deleteById(weddingId);
    }
}
