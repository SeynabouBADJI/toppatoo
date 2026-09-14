package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.MesureDTO;
import com.projet.toppatoo.model.Mesure;
import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.repository.MesureRepository;
import com.projet.toppatoo.repository.PatientRepository;
import com.projet.toppatoo.service.MesureService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MesureServiceImpl implements MesureService {

    private final MesureRepository mesureRepository;
    private final PatientRepository patientRepository;

    // ═══════════════════════════════════════════════════════════════
    // CREATE
    // ═══════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public MesureDTO createMesure(MesureDTO mesureDTO) {
        System.out.println("💾 Création d'une mesure: " + mesureDTO);
        
        // ✅ Calculer le niveau d'alerte automatiquement
        String niveauAlerte = calculerNiveauAlerte(mesureDTO);
        
        Mesure mesure = new Mesure();
        mesure.setType(mesureDTO.getType());
        mesure.setValeur(mesureDTO.getValeur());
        mesure.setValeur2(mesureDTO.getValeur2());
        mesure.setUnite(mesureDTO.getUnite());
        mesure.setNiveauAlerte(niveauAlerte);
        mesure.setNotes(mesureDTO.getNotes());
        mesure.setPatientId(mesureDTO.getPatientId());
        mesure.setConsultationId(mesureDTO.getConsultationId());
        mesure.setDateMesure(LocalDateTime.now());
        
        Mesure saved = mesureRepository.save(mesure);
        System.out.println("✅ Mesure créée avec ID: " + saved.getId() + ", niveau: " + niveauAlerte);
        
        return mapToDTO(saved);
    }

    // ═══════════════════════════════════════════════════════════════
    // CALCUL DU NIVEAU D'ALERTE
    // ═══════════════════════════════════════════════════════════════
    private String calculerNiveauAlerte(MesureDTO mesure) {
        if (mesure == null || mesure.getType() == null || mesure.getValeur() == null) {
            return "NORMAL";
        }
        
        // Récupérer les seuils du patient
        Patient patient = null;
        if (mesure.getPatientId() != null) {
            patient = patientRepository.findById(mesure.getPatientId()).orElse(null);
        }
        
        // ✅ UN SEUL SWITCH, PAS DE SWITCH IMBRIQUÉ
        switch (mesure.getType().toUpperCase()) {
            case "GLYCEMIE":
                return calculerAlerteGlycemie(mesure.getValeur(), patient);
                
            case "TENSION_ARTERIELLE":
                return calculerAlerteTension(
                    mesure.getValeur(), 
                    mesure.getValeur2(), 
                    patient
                );
                
            case "TEMPERATURE":
                return calculerAlerteTemperature(mesure.getValeur());
                
            case "POIDS":
                return calculerAlertePoids(mesure.getValeur(), patient);
                
            case "FREQUENCE_CARDIAQUE":
                return calculerAlerteFrequenceCardiaque(mesure.getValeur());
                
            default:
                return "NORMAL";
        }
    }

    // ─── GLYCÉMIE (g/L) ────────────────────────────────────────────
    private String calculerAlerteGlycemie(Double valeur, Patient patient) {
        Double min = patient != null && patient.getSeuilGlycemieMin() != null 
            ? patient.getSeuilGlycemieMin() : 0.7;
        Double max = patient != null && patient.getSeuilGlycemieMax() != null 
            ? patient.getSeuilGlycemieMax() : 1.1;
        
        // CRITIQUE : < 0.5 ou > 2.0
        if (valeur < 0.5 || valeur > 2.0) return "CRITIQUE";
        // ATTENTION : < min ou > max
        if (valeur < min || valeur > max) return "ATTENTION";
        return "NORMAL";
    }

    // ─── TENSION ARTÉRIELLE (mmHg) ─────────────────────────────────
    private String calculerAlerteTension(Double systolique, Double diastolique, Patient patient) {
        if (systolique == null) return "NORMAL";
        
        double systMax = patient != null && patient.getSeuilTensionSystMax() != null 
            ? patient.getSeuilTensionSystMax() : 140.0;
        double diastMax = patient != null && patient.getSeuilTensionDiastMax() != null 
            ? patient.getSeuilTensionDiastMax() : 90.0;
        
        double syst = systolique;
        double diast = diastolique != null ? diastolique : 0;
        
        // CRITIQUE : >= 180/120 ou < 90/60
        if (syst >= 180 || diast >= 120 || syst < 90 || (diast > 0 && diast < 60)) {
            return "CRITIQUE";
        }
        
        // ATTENTION : >= 140/90 ou >= 130/85
        if (syst >= systMax || diast >= diastMax || syst >= 130 || diast >= 85) {
            return "ATTENTION";
        }
        
        return "NORMAL";
    }

    // ─── TEMPÉRATURE (°C) ──────────────────────────────────────────
    private String calculerAlerteTemperature(Double valeur) {
        if (valeur == null) return "NORMAL";
        
        // CRITIQUE : < 35°C (hypothermie) ou >= 40°C (hyperthermie)
        if (valeur < 35.0 || valeur >= 40.0) return "CRITIQUE";
        
        // ATTENTION : < 36°C ou >= 38°C (fièvre)
        if (valeur < 36.0 || valeur >= 38.0) return "ATTENTION";
        
        return "NORMAL";
    }

    // ─── POIDS (kg) ────────────────────────────────────────────────
    private String calculerAlertePoids(Double valeur, Patient patient) {
        if (valeur == null) return "NORMAL";
        
        Double min = patient != null ? patient.getSeuilPoidsMin() : null;
        Double max = patient != null ? patient.getSeuilPoidsMax() : null;
        
        if (min != null && valeur < min) return "ATTENTION";
        if (max != null && valeur > max) return "ATTENTION";
        
        return "NORMAL";
    }

    // ─── FRÉQUENCE CARDIAQUE (bpm) ─────────────────────────────────
    private String calculerAlerteFrequenceCardiaque(Double valeur) {
        if (valeur == null) return "NORMAL";
        
        // CRITIQUE : < 40 ou > 150
        if (valeur < 40 || valeur > 150) return "CRITIQUE";
        
        // ATTENTION : < 60 ou > 100
        if (valeur < 60 || valeur > 100) return "ATTENTION";
        
        return "NORMAL";
    }

    // ═══════════════════════════════════════════════════════════════
    // GET BY ID
    // ═══════════════════════════════════════════════════════════════
    @Override
    public MesureDTO getMesureById(Long id) {
        Mesure mesure = mesureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesure non trouvée avec l'ID: " + id));
        return mapToDTO(mesure);
    }

    // ═══════════════════════════════════════════════════════════════
    // UPDATE
    // ═══════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public MesureDTO updateMesure(Long id, MesureDTO mesureDTO) {
        Mesure mesure = mesureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesure non trouvée avec l'ID: " + id));
        
        if (mesureDTO.getValeur() != null) {
            mesure.setValeur(mesureDTO.getValeur());
        }
        if (mesureDTO.getValeur2() != null) {
            mesure.setValeur2(mesureDTO.getValeur2());
        }
        if (mesureDTO.getUnite() != null) {
            mesure.setUnite(mesureDTO.getUnite());
        }
        if (mesureDTO.getNiveauAlerte() != null) {
            mesure.setNiveauAlerte(mesureDTO.getNiveauAlerte());
        }
        if (mesureDTO.getNotes() != null) {
            mesure.setNotes(mesureDTO.getNotes());
        }
        
        Mesure updated = mesureRepository.save(mesure);
        return mapToDTO(updated);
    }

    // ═══════════════════════════════════════════════════════════════
    // DELETE
    // ═══════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public void deleteMesure(Long id) {
        if (!mesureRepository.existsById(id)) {
            throw new RuntimeException("Mesure non trouvée avec l'ID: " + id);
        }
        mesureRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════
    // GET BY PATIENT
    // ═══════════════════════════════════════════════════════════════
    @Override
    public List<MesureDTO> getMesuresByPatient(Long patientId, String type) {
        List<Mesure> mesures;
        if (type != null && !type.isEmpty()) {
            mesures = mesureRepository.findByPatientIdAndType(patientId, type);
        } else {
            mesures = mesureRepository.findByPatientId(patientId);
        }
        return mesures.stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // GET ALERTES
    // ═══════════════════════════════════════════════════════════════
    @Override
    public List<MesureDTO> getMesuresAlerte(Long patientId) {
        List<String> niveauxAlerte = Arrays.asList("CRITIQUE", "ATTENTION");
        return mesureRepository.findByPatientIdAndNiveauAlerteIn(patientId, niveauxAlerte)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // GET DERNIÈRES MESURES
    // ═══════════════════════════════════════════════════════════════
    @Override
    public List<MesureDTO> getDernieresMesures(Long patientId, int limit) {
        List<Mesure> mesures = mesureRepository.findByPatientId(patientId);
        
        return mesures.stream()
            .sorted((m1, m2) -> {
                if (m1.getDateMesure() == null && m2.getDateMesure() == null) return 0;
                if (m1.getDateMesure() == null) return 1;
                if (m2.getDateMesure() == null) return -1;
                return m2.getDateMesure().compareTo(m1.getDateMesure());
            })
            .limit(limit)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // GET BY CONSULTATION
    // ═══════════════════════════════════════════════════════════════
    @Override
    public List<MesureDTO> getMesuresByConsultation(Long consultationId) {
        return mesureRepository.findByConsultationId(consultationId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // MAPPING
    // ═══════════════════════════════════════════════════════════════
    
    private MesureDTO mapToDTO(Mesure mesure) {
        MesureDTO dto = new MesureDTO();
        dto.setId(mesure.getId());
        dto.setType(mesure.getType());
        dto.setValeur(mesure.getValeur());
        dto.setValeur2(mesure.getValeur2());
        dto.setUnite(mesure.getUnite());
        dto.setNiveauAlerte(mesure.getNiveauAlerte());
        dto.setDateMesure(mesure.getDateMesure());
        dto.setNotes(mesure.getNotes());
        dto.setPatientId(mesure.getPatientId());
        dto.setConsultationId(mesure.getConsultationId());
        return dto;
    }

    private Mesure mapToEntity(MesureDTO dto) {
        Mesure mesure = new Mesure();
        mesure.setType(dto.getType());
        mesure.setValeur(dto.getValeur());
        mesure.setValeur2(dto.getValeur2());
        mesure.setUnite(dto.getUnite());
        mesure.setNiveauAlerte(dto.getNiveauAlerte());
        mesure.setNotes(dto.getNotes());
        mesure.setPatientId(dto.getPatientId());
        mesure.setConsultationId(dto.getConsultationId());
        return mesure;
    }
}