package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.Mesure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MesureRepository extends JpaRepository<Mesure, Long> {
    
    // ✅ Trouver toutes les mesures d'un patient
    List<Mesure> findByPatientId(Long patientId);
    
    // ✅ Trouver les mesures d'un patient par type
    List<Mesure> findByPatientIdAndType(Long patientId, String type);
    
    // ✅ Trouver les mesures d'un patient par niveau d'alerte
    List<Mesure> findByPatientIdAndNiveauAlerteIn(Long patientId, List<String> niveauxAlerte);
    
    // ✅ Trouver les mesures d'une consultation
    List<Mesure> findByConsultationId(Long consultationId);
    
    // ✅ Trouver les dernières mesures d'un patient
    List<Mesure> findTop10ByPatientIdOrderByDateMesureDesc(Long patientId);
}