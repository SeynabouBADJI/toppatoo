package com.projet.toppatoo.service;

import com.projet.toppatoo.model.RendezVous;
import com.projet.toppatoo.repository.RendezVousRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RendezVousService {

    private final RendezVousRepository rendezVousRepository;

    public List<RendezVous> getAllRendezVous() {
        return rendezVousRepository.findAll();
    }

    public RendezVous getRendezVousById(Long id) {
        return rendezVousRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rendez-vous non trouvé"));
    }

    @Transactional
    public RendezVous createRendezVous(RendezVous rendezVous) {
        rendezVous.setStatut("PLANIFIE");
        return rendezVousRepository.save(rendezVous);
    }

    @Transactional
    public RendezVous updateRendezVous(Long id, RendezVous rendezVous) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous non trouvé");
        }
        rendezVous.setId(id);
        return rendezVousRepository.save(rendezVous);
    }

    @Transactional
    public void deleteRendezVous(Long id) {
        if (!rendezVousRepository.existsById(id)) {
            throw new RuntimeException("Rendez-vous non trouvé");
        }
        rendezVousRepository.deleteById(id);
    }

    @Transactional
    public RendezVous confirmerRendezVous(Long id) {
        RendezVous rdv = getRendezVousById(id);
        rdv.setStatut("CONFIRME");
        return rendezVousRepository.save(rdv);
    }

    @Transactional
    public RendezVous annulerRendezVous(Long id) {
        RendezVous rdv = getRendezVousById(id);
        rdv.setStatut("ANNULE");
        return rendezVousRepository.save(rdv);
    }

    public List<RendezVous> getRendezVousByPatient(Long patientId) {
        return rendezVousRepository.findByPatientId(patientId);
    }

    public List<RendezVous> getRendezVousByMedecin(Long medecinId) {
        return rendezVousRepository.findByMedecinId(medecinId);
    }

    public RendezVous getProchainRendezVous(Long patientId) {
        LocalDateTime now = LocalDateTime.now();
        return rendezVousRepository.findTopByPatientIdAndDateHeureAfterOrderByDateHeureAsc(patientId, now)
                .orElse(null);
    }

    public List<RendezVous> getRendezVousAujourdhui() {
        LocalDateTime debut = LocalDateTime.now().withHour(0).withMinute(0);
        LocalDateTime fin = LocalDateTime.now().withHour(23).withMinute(59);
        return rendezVousRepository.findRdvPourRappel(debut, fin);
    }
}