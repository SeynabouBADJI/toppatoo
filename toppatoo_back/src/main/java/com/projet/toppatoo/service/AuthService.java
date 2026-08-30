package com.projet.toppatoo.service;

import com.projet.toppatoo.model.Medecin;
import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.model.User;
import com.projet.toppatoo.repository.MedecinRepository;
import com.projet.toppatoo.repository.PatientRepository;
import com.projet.toppatoo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final MedecinRepository medecinRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    // ─── CONNEXION ───────────────────────────────────────────────
    public Map<String, Object> login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        if (!user.getActif()) {
            throw new RuntimeException("Compte désactivé. Contactez l'administrateur.");
        }

        // Récupérer l'ID métier selon le rôle
        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("role", user.getRole());
        response.put("nom", user.getNom());
        response.put("prenom", user.getPrenom());
        response.put("email", user.getEmail());

        if (user.getRole().equals("MEDECIN")) {
            Medecin medecin = medecinRepository.findByUserId(user.getId()).orElse(null);
            if (medecin != null) {
                response.put("medecinId", medecin.getId());
            }
        } else if (user.getRole().equals("PATIENT")) {
            Patient patient = patientRepository.findByUserId(user.getId()).orElse(null);
            if (patient != null) {
                response.put("patientId", patient.getId());
            }
        }

        return response;
    }

    // ─── INSCRIPTION MÉDECIN ─────────────────────────────────────
    @Transactional
    public Medecin registerMedecin(
            String nom, 
            String prenom, 
            String email,
            String password, 
            String telephone,
            String specialite, 
            String etablissement, 
            String ville
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Un compte existe déjà avec cet email.");
        }

        User user = User.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .password(passwordEncoder.encode(password))
                .telephone(telephone)
                .role("MEDECIN")
                .actif(true)
                .build();

        User userSauve = userRepository.save(user);

        Medecin medecin = Medecin.builder()
                .user(userSauve)
                .specialite(specialite)
                .adresseCabinet(etablissement)
                .build();

        return medecinRepository.save(medecin);
    }

    // ─── INSCRIPTION PATIENT ─────────────────────────────────────
    @Transactional
    public Patient registerPatient(
            String nom, 
            String prenom, 
            String email,
            String password, 
            String telephone,
            LocalDate dateNaissance, 
            String sexe,
            String maladie, 
            Long medecinId
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Un compte existe déjà avec cet email.");
        }

        Medecin medecin = null;
        if (medecinId != null) {
            medecin = medecinRepository.findById(medecinId)
                    .orElseThrow(() -> new RuntimeException("Médecin introuvable."));
        }

        User user = User.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .password(passwordEncoder.encode(password))
                .telephone(telephone)
                .role("PATIENT")
                .actif(true)
                .build();

        User userSauve = userRepository.save(user);

        Patient patient = Patient.builder()
                .user(userSauve)
                .medecinId(medecin != null ? medecin.getId() : null)
                .medecinNom(medecin != null ? medecin.getUser().getNom() + " " + medecin.getUser().getPrenom() : null)
                .dateNaissance(dateNaissance)
                .sexe(sexe)
                .maladieChronique(maladie)
                .build();

        return patientRepository.save(patient);
    }

    // ─── RÉINITIALISATION DU MOT DE PASSE ──────────────────────
    @Transactional
    public void resetPassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Aucun compte avec cet email."));
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // ─── VÉRIFICATION DE L'EMAIL ────────────────────────────────
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    // ─── ACTIVATION / DÉSACTIVATION DU COMPTE ──────────────────
    @Transactional
    public void activerCompte(Long userId, boolean actif) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        user.setActif(actif);
        userRepository.save(user);
    }
}