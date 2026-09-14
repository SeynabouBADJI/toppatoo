package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.LigneMedicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LigneMedicamentRepository extends JpaRepository<LigneMedicament, Long> {
    List<LigneMedicament> findByOrdonnanceId(Long ordonnanceId);
}