package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.model.User;
import com.projet.toppatoo.repository.PatientRepository;
import com.projet.toppatoo.repository.UserRepository;
import com.projet.toppatoo.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional(readOnly = true)
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findById(Long id) {
        return patientRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Patient> findByUserId(Long userId) {
        return patientRepository.findByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByMedecin(Long medecinId) {
        List<Patient> patients = patientRepository.findByMedecinId(medecinId);
        // Force le chargement du user (EAGER déjà configuré)
        patients.forEach(p -> {
            if (p.getUser() != null) p.getUser().getEmail();
        });
        return patients;
    }

    @Override
    @Transactional
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    @Transactional
    public Patient updatePatient(Long id, Patient modifications) {
        Patient patient = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient introuvable : " + id));
        patient.setAntecedents(modifications.getAntecedents());
        patient.setAllergies(modifications.getAllergies());
        patient.setGroupeSanguin(modifications.getGroupeSanguin());
        patient.setAdresse(modifications.getAdresse());
        patient.setVille(modifications.getVille());
        return patientRepository.save(patient);
    }

    @Override
    @Transactional
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByMaladieChronique(String maladie) {
        return patientRepository.findAll().stream()
                .filter(p -> maladie.equalsIgnoreCase(p.getMaladieChronique()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Patient> findByVille(String ville) {
        return patientRepository.findAll().stream()
                .filter(p -> ville.equalsIgnoreCase(p.getVille()))
                .toList();
    }

    @Override
    @Transactional
    public PatientCreeResult enregistrer(String nom, String prenom, String email,
                                          String telephone, LocalDate dateNaissance,
                                          String sexe, String maladie, Long medecinId) {
        String mdpClair = genererMotDePasse();

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setTelephone(telephone);
        user.setPassword(passwordEncoder.encode(mdpClair));
        user.setRole("PATIENT");
        user.setActif(true);
        User userSauve = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(userSauve)
                .medecinId(medecinId)
                .dateNaissance(dateNaissance)
                .sexe(sexe)
                .maladieChronique(maladie)
                .build();

        return new PatientCreeResult(patientRepository.save(patient), mdpClair);
    }

    @Override
    @Transactional
    public Patient mettreAJourProfil(Long id, Patient modifications) {
        return updatePatient(id, modifications);
    }

    @Override
    @Transactional
    public Patient mettreAJourSeuils(Long id, Double glycemieMin, Double glycemieMax,
                                      Double tensionSystMax, Double tensionDiastMax,
                                      Double poidsMin, Double poidsMax) {
        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient introuvable : " + id));
        p.setSeuilGlycemieMin(glycemieMin);
        p.setSeuilGlycemieMax(glycemieMax);
        p.setSeuilTensionSystMax(tensionSystMax);
        p.setSeuilTensionDiastMax(tensionDiastMax);
        p.setSeuilPoidsMin(poidsMin);
        p.setSeuilPoidsMax(poidsMax);
        return patientRepository.save(p);
    }

    @Override
    @Transactional
    public Patient mettreAJourMedecinTraitant(Long id, Long medecinId) {
        Patient p = patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient introuvable : " + id));
        p.setMedecinId(medecinId);
        return patientRepository.save(p);
    }

    @Override
    public long countPatients() { return patientRepository.count(); }

    @Override
    public long countPatientsByMedecin(Long medecinId) {
        return patientRepository.findByMedecinId(medecinId).size();
    }

    @Override
    public long countPatientsByMaladie(String maladie) {
        return findByMaladieChronique(maladie).size();
    }

    private String genererMotDePasse() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) sb.append(CHARS.charAt(RANDOM.nextInt(CHARS.length())));
        return sb.toString();
    }
}