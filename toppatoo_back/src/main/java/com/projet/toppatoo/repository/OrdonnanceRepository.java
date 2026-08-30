package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.Ordonnance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrdonnanceRepository extends JpaRepository<Ordonnance, Long> {
    
    Optional<Ordonnance> findByConsultationId(Long consultationId);
}