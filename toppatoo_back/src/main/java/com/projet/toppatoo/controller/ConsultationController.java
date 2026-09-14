package com.projet.toppatoo.controller;

import com.projet.toppatoo.dto.ConsultationCreateDTO;
import com.projet.toppatoo.dto.ConsultationDTO;
import com.projet.toppatoo.dto.MesureDTO;
import com.projet.toppatoo.dto.OrdonnanceDTO;
import com.projet.toppatoo.service.ConsultationService;
import com.projet.toppatoo.service.MesureService;
import com.projet.toppatoo.service.OrdonnanceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ConsultationController {

    private final ConsultationService consultationService;
    private final MesureService mesureService;  
    private final OrdonnanceService ordonnanceService;  


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

    // ══════════════════════════════════════════════════════════════
    // ✅ AJOUTER LES ENDPOINTS POUR LES MESURES
    // ══════════════════════════════════════════════════════════════

    // ── AJOUTER une mesure à une consultation ─────────────────────
    @PostMapping("/{consultationId}/mesures")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<MesureDTO> ajouterMesure(
            @PathVariable Long consultationId,
            @Valid @RequestBody MesureDTO mesureDTO
    ) {
        // ✅ LOGS POUR DÉBOGUER
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("=========================================");
        System.out.println("📝 Ajout mesure à consultation: " + consultationId);
        System.out.println("👤 Utilisateur: " + (auth != null ? auth.getName() : "null"));
        System.out.println("🎭 Authorities: " + (auth != null ? auth.getAuthorities() : "null"));
        System.out.println("🔐 Authentifié: " + (auth != null && auth.isAuthenticated()));
        System.out.println("📊 Mesure: " + mesureDTO);
        System.out.println("=========================================");

        // Associer la mesure à la consultation
        mesureDTO.setConsultationId(consultationId);
        
        MesureDTO saved = mesureService.createMesure(mesureDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ── LIRE les mesures d'une consultation ───────────────────────
    @GetMapping("/{consultationId}/mesures")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<MesureDTO>> getMesuresByConsultation(
            @PathVariable Long consultationId
    ) {
        return ResponseEntity.ok(mesureService.getMesuresByConsultation(consultationId));
    }

    // ── SUPPRIMER une mesure d'une consultation ───────────────────
    @DeleteMapping("/{consultationId}/mesures/{mesureId}")
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<Void> supprimerMesure(
            @PathVariable Long consultationId,
            @PathVariable Long mesureId
    ) {
        mesureService.deleteMesure(mesureId);
        return ResponseEntity.noContent().build();
    }

     @PostMapping("/{consultationId}/ordonnance")
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<OrdonnanceDTO> ajouterOrdonnance(
            @PathVariable Long consultationId,
            @Valid @RequestBody OrdonnanceDTO ordonnanceDTO
    ) {
        System.out.println("📝 Ajout ordonnance à consultation: " + consultationId);
        System.out.println("💊 Médicaments: " + (ordonnanceDTO.getMedicaments() != null 
                ? ordonnanceDTO.getMedicaments().size() : 0));
        
        OrdonnanceDTO saved = ordonnanceService.createOrdonnance(consultationId, ordonnanceDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{consultationId}/ordonnance")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<OrdonnanceDTO> getOrdonnance(
            @PathVariable Long consultationId
    ) {
        return ResponseEntity.ok(ordonnanceService.getByConsultation(consultationId));
    }

}