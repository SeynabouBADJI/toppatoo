package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {

    List<RendezVous> findByPatientId(Long patientId);

    List<RendezVous> findByMedecinId(Long medecinId);

    // ✅ Prochain RDV (après maintenant, trié par date croissante)
    Optional<RendezVous> findTopByPatientIdAndDateHeureAfterOrderByDateHeureAsc(
            Long patientId, 
            LocalDateTime date
    );

    // ✅ RDV à venir d'un patient
    List<RendezVous> findByPatientIdAndDateHeureAfterOrderByDateHeureAsc(
            Long patientId, 
            LocalDateTime date
    );

    // ✅ RDV d'un médecin triés par date
    List<RendezVous> findByMedecinIdOrderByDateHeureAsc(Long medecinId);
}