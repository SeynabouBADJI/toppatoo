package com.projet.toppatoo.controller;

import com.projet.toppatoo.dto.ConsultationCreateDTO;
import com.projet.toppatoo.dto.ConsultationDTO;
import com.projet.toppatoo.service.ConsultationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConsultationController {

    private final ConsultationService consultationService;

    // ── CRÉER une consultation ─────────────────────────────────────
    @PostMapping
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<ConsultationDTO> creerConsultation(
            @Valid @RequestBody ConsultationCreateDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consultationService.creerConsultation(request));
    }

    // ── LIRE une consultation ──────────────────────────────────────
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<ConsultationDTO> getConsultationById(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    // ── LIRE les consultations d'un patient ───────────────────────
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<ConsultationDTO>> getConsultationsByPatient(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(consultationService.getConsultationsByPatient(patientId));
    }

    // ── DERNIÈRE consultation d'un patient ────────────────────────
    @GetMapping("/patient/{patientId}/derniere")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<ConsultationDTO> getDerniereConsultation(
            @PathVariable Long patientId
    ) {
        ConsultationDTO dto = consultationService.getDerniereConsultation(patientId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    // ── MODIFIER une consultation ──────────────────────────────────
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<ConsultationDTO> updateConsultation(
            @PathVariable Long id,
            @Valid @RequestBody ConsultationDTO consultationDTO
    ) {
        return ResponseEntity.ok(consultationService.updateConsultation(id, consultationDTO));
    }

    // ── SUPPRIMER une consultation ─────────────────────────────────
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }
}