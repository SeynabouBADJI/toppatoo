package com.projet.toppatoo.controller;

import com.projet.toppatoo.model.RendezVous;
import com.projet.toppatoo.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousRepository rendezVousRepository;

    @GetMapping
    @PreAuthorize("hasAnyRole('MEDECIN', 'ADMIN')")
    public ResponseEntity<List<RendezVous>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousRepository.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> getRendezVousById(@PathVariable Long id) {
        return rendezVousRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<RendezVous> createRendezVous(@RequestBody RendezVous rendezVous) {
        return ResponseEntity.ok(rendezVousRepository.save(rendezVous));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> updateRendezVous(
            @PathVariable Long id,
            @RequestBody RendezVous rendezVous
    ) {
        if (!rendezVousRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rendezVous.setId(id);
        return ResponseEntity.ok(rendezVousRepository.save(rendezVous));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRendezVous(@PathVariable Long id) {
        if (!rendezVousRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        rendezVousRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/confirmer")
    @PreAuthorize("hasRole('MEDECIN')")
    public ResponseEntity<RendezVous> confirmerRendezVous(@PathVariable Long id) {
        return rendezVousRepository.findById(id)
            .map(rdv -> {
                rdv.setStatut("CONFIRME");
                return ResponseEntity.ok(rendezVousRepository.save(rdv));
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/annuler")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<RendezVous> annulerRendezVous(@PathVariable Long id) {
        return rendezVousRepository.findById(id)
            .map(rdv -> {
                rdv.setStatut("ANNULE");
                return ResponseEntity.ok(rendezVousRepository.save(rdv));
            })
            .orElse(ResponseEntity.notFound().build());
    }
}