package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.Medecin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedecinRepository extends JpaRepository<Medecin, Long> {
    
    Optional<Medecin> findByUserId(Long userId);
    List<Medecin> findBySpecialite(String specialite);
    List<Medecin> findByVille(String ville);  // ✅ Ajouté
    List<Medecin> findByEtablissement(String etablissement);  // ✅ Ajouté
    long countBySpecialite(String specialite);  // ✅ Ajouté
    
    @Query("SELECT m FROM Medecin m WHERE " +
           "(:specialite IS NULL OR m.specialite = :specialite) AND " +
           "(:ville IS NULL OR m.ville = :ville)")
    List<Medecin> findByFilters(
            @Param("specialite") String specialite,
            @Param("ville") String ville
    );
}