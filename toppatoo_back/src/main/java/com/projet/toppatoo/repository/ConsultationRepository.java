package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsultationRepository extends JpaRepository<Consultation, Long> {
    List<Consultation> findByPatientId(Long patientId);
    List<Consultation> findByMedecinId(Long medecinId);
    Optional<Consultation> findFirstByPatientIdOrderByDateConsultationDesc(Long patientId);
}