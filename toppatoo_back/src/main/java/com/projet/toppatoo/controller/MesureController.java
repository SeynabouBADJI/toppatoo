package com.projet.toppatoo.controller;

import com.projet.toppatoo.dto.MesureDTO;
import com.projet.toppatoo.service.MesureService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/mesures")
@RequiredArgsConstructor
public class MesureController {

    private final MesureService mesureService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<MesureDTO> getMesureById(@PathVariable Long id) {
        return ResponseEntity.ok(mesureService.getMesureById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<MesureDTO> updateMesure(
            @PathVariable Long id,
            @Valid @RequestBody MesureDTO mesureDTO
    ) {
        return ResponseEntity.ok(mesureService.updateMesure(id, mesureDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<Void> deleteMesure(@PathVariable Long id) {
        mesureService.deleteMesure(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<MesureDTO>> getMesuresByPatient(
            @PathVariable Long patientId,
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(mesureService.getMesuresByPatient(patientId, type));
    }

    @GetMapping("/patient/{patientId}/alertes")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<MesureDTO>> getMesuresAlerte(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(mesureService.getMesuresAlerte(patientId));
    }

    // ✅ Nouvelle méthode : Ajouter une mesure
    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<MesureDTO> createMesure(@Valid @RequestBody MesureDTO mesureDTO) {
        return ResponseEntity.ok(mesureService.createMesure(mesureDTO));
    }

    // ✅ Nouvelle méthode : Récupérer les dernières mesures par patient
    @GetMapping("/patient/{patientId}/dernieres")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<MesureDTO>> getDernieresMesures(
            @PathVariable Long patientId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(mesureService.getDernieresMesures(patientId, limit));
    }
}