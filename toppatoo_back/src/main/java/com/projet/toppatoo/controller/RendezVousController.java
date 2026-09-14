package com.projet.toppatoo.controller;

import com.projet.toppatoo.model.RendezVous;
import com.projet.toppatoo.service.RendezVousService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RendezVousController {

    private final RendezVousService rendezVousService;

    // ═══════════════════════════════════════════════════════════════
    // GET ALL
    // ═══════════════════════════════════════════════════════════════
    @GetMapping
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<List<RendezVous>> getAllRendezVous(
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Long medecinId
    ) {
        if (medecinId != null) {
            return ResponseEntity.ok(rendezVousService.getByMedecin(medecinId));
        }
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    // ═══════════════════════════════════════════════════════════════
    // GET BY ID
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable Long id) {
        return rendezVousService.getRendezVousById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ PROCHAIN RDV D'UN PATIENT (manquant !)
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/patient/{patientId}/prochain")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> getProchainRendezVous(
            @PathVariable Long patientId
    ) {
        System.out.println("📅 Recherche prochain RDV pour patient: " + patientId);
        RendezVous rdv = rendezVousService.getProchainRendezVous(patientId);
        return rdv != null 
            ? ResponseEntity.ok(rdv) 
            : ResponseEntity.notFound().build();
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ RDV D'UN PATIENT
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<RendezVous>> getRendezVousByPatient(
            @PathVariable Long patientId
    ) {
        return ResponseEntity.ok(rendezVousService.getByPatient(patientId));
    }

    // ═══════════════════════════════════════════════════════════════
    // ✅ RDV D'UN MÉDECIN
    // ═══════════════════════════════════════════════════════════════
    @GetMapping("/medecin/{medecinId}")
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<List<RendezVous>> getRendezVousByMedecin(
            @PathVariable Long medecinId
    ) {
        return ResponseEntity.ok(rendezVousService.getByMedecin(medecinId));
    }

    // ═══════════════════════════════════════════════════════════════
    // POST — Créer un RDV
    // ═══════════════════════════════════════════════════════════════
    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> createRendezVous(@Valid @RequestBody RendezVous rdv) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(rendezVousService.createRendezVous(rdv));
    }

    // ═══════════════════════════════════════════════════════════════
    // PUT — Modifier
    // ═══════════════════════════════════════════════════════════════
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> updateRendezVous(
            @PathVariable Long id,
            @Valid @RequestBody RendezVous rdv
    ) {
        return ResponseEntity.ok(rendezVousService.updateRendezVous(id, rdv));
    }

    // ═══════════════════════════════════════════════════════════════
    // PUT — Confirmer
    // ═══════════════════════════════════════════════════════════════
    @PutMapping("/{id}/confirmer")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<RendezVous> confirmerRendezVous(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.confirmerRendezVous(id));
    }

    // ═══════════════════════════════════════════════════════════════
    // PUT — Annuler
    // ═══════════════════════════════════════════════════════════════
    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> annulerRendezVous(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.annulerRendezVous(id));
    }

    // ═══════════════════════════════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════════════════════════════
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        rendezVousService.deleteRendezVous(id);
        return ResponseEntity.noContent().build();
    }
}