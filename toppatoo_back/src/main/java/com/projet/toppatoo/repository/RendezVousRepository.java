package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.RendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    
    List<RendezVous> findByPatientId(Long patientId);
    List<RendezVous> findByMedecinId(Long medecinId);
    Optional<RendezVous> findFirstByPatientIdAndStatutOrderByDateHeureAsc(Long patientId, String statut);
    List<RendezVous> findByPatientIdAndDateHeureAfterOrderByDateHeureAsc(Long patientId, LocalDateTime date);
    Optional<RendezVous> findTopByPatientIdAndDateHeureAfterOrderByDateHeureAsc(Long patientId, LocalDateTime date);
    List<RendezVous> findByMedecinIdOrderByDateHeureAsc(Long medecinId);
    
    @Query("SELECT r FROM RendezVous r WHERE r.dateHeure BETWEEN :debut AND :fin AND r.statut = 'PLANIFIE'")
    List<RendezVous> findRdvPourRappel(@Param("debut") LocalDateTime debut, @Param("fin") LocalDateTime fin);
}