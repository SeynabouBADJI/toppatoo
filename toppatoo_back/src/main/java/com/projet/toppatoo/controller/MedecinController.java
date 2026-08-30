package com.projet.toppatoo.controller;

import com.projet.toppatoo.model.Medecin;
import com.projet.toppatoo.service.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/medecins")
@RequiredArgsConstructor
public class MedecinController {

    private final MedecinService medecinService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MEDECIN')")
    public ResponseEntity<List<Medecin>> getAllMedecins() {
        return ResponseEntity.ok(medecinService.getAllMedecins());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<Medecin> getMedecinById(@PathVariable Long id) {
        return medecinService.findById(id)  // ✅ Utiliser findById
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<Medecin> getMedecinByUserId(@PathVariable Long userId) {
        return medecinService.findByUserId(userId)  // ✅ Utiliser findByUserId
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/specialite/{specialite}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<Medecin>> getMedecinsBySpecialite(@PathVariable String specialite) {
        return ResponseEntity.ok(medecinService.findBySpecialite(specialite));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Medecin> createMedecin(@Valid @RequestBody Medecin medecin) {
        return ResponseEntity.ok(medecinService.save(medecin));  // ✅ Utiliser save
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<Medecin> updateMedecin(
            @PathVariable Long id,
            @Valid @RequestBody Medecin medecin
    ) {
        return ResponseEntity.ok(medecinService.updateMedecin(id, medecin));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMedecin(@PathVariable Long id) {
        medecinService.deleteMedecin(id);
        return ResponseEntity.noContent().build();
    }
}