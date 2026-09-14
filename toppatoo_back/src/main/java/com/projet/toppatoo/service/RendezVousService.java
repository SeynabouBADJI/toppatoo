package com.projet.toppatoo.service;

import com.projet.toppatoo.model.RendezVous;
import com.projet.toppatoo.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    public Optional<RendezVous> getRendezVousById(Long id) {
        return rendezVousRepository.findById(id);
    }

    public List<RendezVous> getByPatient(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

    public List<RendezVous> getByMedecin(Long medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    // ✅ Prochain RDV d'un patient
    public RendezVous getProchainRendezVous(Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println("📅 Recherche prochain RDV pour patient " + patientId + " après " + now);
        
        return rendezVousRepository
            .findTopByPatientIdAndDateHeureAfterOrderByDateHeureAsc(patientId, now)
            .orElse(null);
    }

    @Transactional
    public RendezVous createRendezVous(RendezVous rdv) {
        rdv.setStatut("PLANIFIE");
        return rendezVousRepository.save(rdv);
    }

    @Transactional
    public RendezVous updateRendezVous(Long id, RendezVous rdv) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous non trouvé: " + id);
        }
        rdv.setId(id);
        return rendezVousRepository.save(rdv);
    }

    @Transactional
    public RendezVous confirmerRendezVous(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé: " + id));
        rdv.setStatut("CONFIRME");
        return rendezVousRepository.save(rdv);
    }

    @Transactional
    public RendezVous annulerRendezVous(Long id) {
        RendezVous rdv = rendezVousRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé: " + id));
        rdv.setStatut("ANNULE");
        return rendezVousRepository.save(rdv);
    }

    @Transactional
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous non trouvé: " + id);
        }
        rendezVousRepository.deleteById(id);
    }
}