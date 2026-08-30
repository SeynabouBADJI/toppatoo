package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.model.Medecin;
import com.projet.toppatoo.model.User;
import com.projet.toppatoo.repository.MedecinRepository;
import com.projet.toppatoo.repository.UserRepository;
import com.projet.toppatoo.service.MedecinService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MedecinServiceImpl implements MedecinService {

    private final MedecinRepository medecinRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String CARACTERES = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final SecureRandom RANDOM = new SecureRandom();

    private String genererMotDePasseTemporaire() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(CARACTERES.charAt(RANDOM.nextInt(CARACTERES.length())));
        }
        return sb.toString();
    }

    @Override
    public List<Medecin> getAllMedecins() {
        return medecinRepository.findAll();
    }

    @Override
    public Optional<Medecin> findById(Long id) {
        return medecinRepository.findById(id);
    }

    @Override
    public Optional<Medecin> findByUserId(Long userId) {
        return medecinRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Medecin save(Medecin medecin) {
        return medecinRepository.save(medecin);
    }

    @Override
    @Transactional
    public Medecin updateMedecin(Long id, Medecin medecin) {
        if (!medecinRepository.existsById(id)) {
            throw new RuntimeException("Médecin introuvable avec l'ID: " + id);
        }
        medecin.setId(id);
        return medecinRepository.save(medecin);
    }

    @Override
    @Transactional
    public void deleteMedecin(Long id) {
        Medecin medecin = medecinRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médecin introuvable avec l'ID: " + id));
        if (medecin.getUser() != null) {
            userRepository.delete(medecin.getUser());
        }
        medecinRepository.delete(medecin);
    }

    @Override
    public List<Medecin> findBySpecialite(String specialite) {
        return medecinRepository.findBySpecialite(specialite);
    }

    @Override
    public List<Medecin> findByVille(String ville) {
        return medecinRepository.findByVille(ville);
    }

    @Override
    public List<Medecin> findByEtablissement(String etablissement) {
        return medecinRepository.findByEtablissement(etablissement);
    }

    @Override
    @Transactional
    public MedecinCreeResult enregistrer(
            String nom, String prenom, String email, String password,
            String telephone, String specialite, String etablissement,
            String ville, String numeroRpps, String telephoneCabinet,
            String adresseCabinet
    ) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Un compte existe déjà avec cet email.");
        }

        String motDePasseClair = password;
        boolean motDePasseGenere = false;
        if (motDePasseClair == null || motDePasseClair.isEmpty()) {
            motDePasseClair = genererMotDePasseTemporaire();
            motDePasseGenere = true;
        }

        User user = User.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .telephone(telephone)
                .password(passwordEncoder.encode(motDePasseClair))
                .role("MEDECIN")
                .actif(true)
                .build();

        User userSauve = userRepository.save(user);

        Medecin medecin = Medecin.builder()
                .user(userSauve)
                .specialite(specialite)
                .etablissement(etablissement)
                .ville(ville)
                .numeroRpps(numeroRpps)
                .telephoneCabinet(telephoneCabinet)
                .adresseCabinet(adresseCabinet)
                .build();

        Medecin medecinSauve = medecinRepository.save(medecin);

        return new MedecinCreeResult(medecinSauve, motDePasseGenere ? motDePasseClair : null);
    }

    @Override
    public long countMedecins() {
        return medecinRepository.count();
    }

    @Override
    public long countMedecinsBySpecialite(String specialite) {
        return medecinRepository.countBySpecialite(specialite);
    }
}