package com.projet.toppatoo.service;

import com.projet.toppatoo.model.Medecin;
import java.util.List;
import java.util.Optional;

public interface MedecinService {
    
    List<Medecin> getAllMedecins();
    Optional<Medecin> findById(Long id);
    Optional<Medecin> findByUserId(Long userId);
    Medecin save(Medecin medecin);
    Medecin updateMedecin(Long id, Medecin medecin);
    void deleteMedecin(Long id);
    List<Medecin> findBySpecialite(String specialite);
    List<Medecin> findByVille(String ville);
    List<Medecin> findByEtablissement(String etablissement);
    
    MedecinCreeResult enregistrer(
        String nom, String prenom, String email, String password,
        String telephone, String specialite, String etablissement,
        String ville, String numeroRpps, String telephoneCabinet,
        String adresseCabinet
    );
    
    long countMedecins();
    long countMedecinsBySpecialite(String specialite);
    
    record MedecinCreeResult(Medecin medecin, String motDePasseTemporaire) {}
}