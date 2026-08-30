package com.projet.toppatoo.repository;

import com.projet.toppatoo.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // ✅ Trouver toutes les notifications d'un patient
    List<Notification> findByPatientId(Long patientId);
    
    // ✅ Trouver les notifications non lues d'un patient
    List<Notification> findByPatientIdAndLueFalse(Long patientId);
    
    // ✅ Compter les notifications non lues d'un patient
    long countByPatientIdAndLueFalse(Long patientId);
    
    // ✅ Trouver les notifications par type
    List<Notification> findByPatientIdAndType(Long patientId, String type);
    
    // ✅ Trouver les notifications non lues par type
    List<Notification> findByPatientIdAndLueFalseAndType(Long patientId, String type);
}