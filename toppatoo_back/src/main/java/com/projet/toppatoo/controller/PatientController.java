package com.projet.toppatoo.controller;

import com.projet.toppatoo.dto.PatientDTO;
import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.service.PatientService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    public ResponseEntity<List<PatientDTO>> getAll() {
        return ResponseEntity.ok(patientService.getAllPatients()
                .stream().map(PatientDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientDTO> findById(@PathVariable Long id) {
        return patientService.findById(id)
                .map(p -> ResponseEntity.ok(PatientDTO.from(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<PatientDTO>> findByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(patientService.findByMedecin(medecinId)
                .stream().map(PatientDTO::from).collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<PatientDTO> findByUser(@PathVariable Long userId) {
        return patientService.findByUserId(userId)
                .map(p -> ResponseEntity.ok(PatientDTO.from(p)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> enregistrer(@RequestBody PatientRequest req) {
        PatientService.PatientCreeResult result = patientService.enregistrer(
                req.getNom(), req.getPrenom(), req.getEmail(),
                req.getTelephone(), req.getDateNaissance(),
                req.getSexe(), req.getMaladieChronique(), req.getMedecinId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "patientId", result.patient().getId(),
                "email", req.getEmail(),
                "motDePasseTemporaire", result.motDePasseTemporaire(),
                "message", "Patient créé avec succès"
        ));
    }

    @PutMapping("/{id}/profil")
    public ResponseEntity<PatientDTO> mettreAJourProfil(@PathVariable Long id,
                                                          @RequestBody Patient patient) {
        return ResponseEntity.ok(PatientDTO.from(patientService.mettreAJourProfil(id, patient)));
    }

    @PutMapping("/{id}/seuils")
    public ResponseEntity<PatientDTO> mettreAJourSeuils(@PathVariable Long id,
                                                          @RequestBody SeuilsRequest req) {
        return ResponseEntity.ok(PatientDTO.from(patientService.mettreAJourSeuils(
                id, req.getGlycemieMin(), req.getGlycemieMax(),
                req.getTensionSystMax(), req.getTensionDiastMax(),
                req.getPoidsMin(), req.getPoidsMax())));
    }

    @Data public static class PatientRequest {
        private String nom, prenom, email, telephone, sexe, maladieChronique;
        private LocalDate dateNaissance;
        private Long medecinId;
    }

    @Data public static class SeuilsRequest {
        private Double glycemieMin, glycemieMax, poidsMin, poidsMax;
        private Double tensionSystMax, tensionDiastMax;
    }
}