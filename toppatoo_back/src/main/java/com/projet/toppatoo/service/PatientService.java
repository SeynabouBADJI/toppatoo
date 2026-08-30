package com.projet.toppatoo.service;

import com.projet.toppatoo.model.Patient;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    
    // ─── CRUD ──────────────────────────────────────────────────────
    List<Patient> getAllPatients();
    Optional<Patient> findById(Long id);
    Optional<Patient> findByUserId(Long userId);
    Patient save(Patient patient);
    Patient updatePatient(Long id, Patient patient);
    void deletePatient(Long id);
    
    // ─── RECHERCHE ─────────────────────────────────────────────────
    List<Patient> findByMedecin(Long medecinId);
    List<Patient> findByMaladieChronique(String maladie);
    List<Patient> findByVille(String ville);
    
    // ─── ENREGISTREMENT ───────────────────────────────────────────
    PatientCreeResult enregistrer(
        String nom, 
        String prenom, 
        String email,
        String telephone, 
        LocalDate dateNaissance,
        String sexe, 
        String maladie,
        Long medecinId
    );
    
    // ─── MISE À JOUR ──────────────────────────────────────────────
    Patient mettreAJourProfil(Long id, Patient modifications);
    Patient mettreAJourSeuils(
        Long id,
        Double glycemieMin, 
        Double glycemieMax,
        Double tensionSystMax, 
        Double tensionDiastMax,
        Double poidsMin, 
        Double poidsMax
    );
    Patient mettreAJourMedecinTraitant(Long id, Long medecinId);
    
    // ─── STATISTIQUES ─────────────────────────────────────────────
    long countPatients();
    long countPatientsByMedecin(Long medecinId);
    long countPatientsByMaladie(String maladie);
    
    // ─── RESULTAT ─────────────────────────────────────────────────
    record PatientCreeResult(Patient patient, String motDePasseTemporaire) {}
}