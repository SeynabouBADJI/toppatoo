package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.*;
import com.projet.toppatoo.model.*;
import com.projet.toppatoo.repository.*;
import com.projet.toppatoo.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final MesureRepository mesureRepository;
    private final OrdonnanceRepository ordonnanceRepository;
    private final LigneMedicamentRepository ligneMedicamentRepository;

    @Override
    public ConsultationDTO getConsultationById(Long id) {
        return mapToDTO(consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + id)));
    }

    @Override
    @Transactional
    public ConsultationDTO creerConsultation(ConsultationCreateDTO request) {
        Patient patient = patientRepository.findById(request.getPatientId())
            .orElseThrow(() -> new RuntimeException("Patient non trouvé: " + request.getPatientId()));

        Consultation consultation = new Consultation();
        consultation.setPatientId(request.getPatientId());
        consultation.setMedecinId(request.getMedecinId());
        consultation.setCompteRendu(request.getCompteRendu());
        consultation.setObjectifsMois(request.getObjectifsMois());
        consultation.setType(request.getType());
        consultation.setDateConsultation(LocalDateTime.now());
        if (patient.getMedecinNom() != null) {
            consultation.setMedecinNom(patient.getMedecinNom());
        }

        return mapToDTO(consultationRepository.save(consultation));
    }

    @Override
    @Transactional
    public MesureDTO ajouterMesure(Long consultationId, MesureCreateDTO request) {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + consultationId));

        // Déterminer le niveau d'alerte
        String niveauAlerte = calculerNiveauAlerte(
            request.getType(), request.getValeur(), consultation.getPatientId()
        );

        Mesure mesure = new Mesure();
        mesure.setType(request.getType());
        mesure.setValeur(request.getValeur());
        mesure.setValeur2(request.getValeur2());
        mesure.setUnite(request.getUnite());
        mesure.setNotes(request.getNotes());
        mesure.setNiveauAlerte(niveauAlerte);
        mesure.setDateMesure(LocalDateTime.now());
        mesure.setConsultationId(consultationId);
        mesure.setPatientId(consultation.getPatientId());

        return MesureDTO.from(mesureRepository.save(mesure));
    }

    @Override
    @Transactional
    public OrdonnanceDTO ajouterOrdonnance(Long consultationId, OrdonnanceCreateDTO request) {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + consultationId));

        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setNotes(request.getNotes());
        ordonnance.setDateExpiration(request.getDateExpiration());
        ordonnance.setConsultationId(consultationId);
        Ordonnance savedOrdonnance = ordonnanceRepository.save(ordonnance);

        if (request.getMedicaments() != null) {
            request.getMedicaments().forEach(m -> {
                LigneMedicament ligne = new LigneMedicament();
                ligne.setNomMedicament(m.getNomMedicament());
                ligne.setDosage(m.getDosage());
                ligne.setPosologie(m.getPosologie());
                ligne.setDureeJours(m.getDureeJours());
                ligne.setHeuresRappel(m.getHeuresRappel());
                ligne.setInstructions(m.getInstructions());
                ligne.setOrdonnanceId(savedOrdonnance.getId());
                ligneMedicamentRepository.save(ligne);
            });
        }

        consultation.setOrdonnance(savedOrdonnance);
        consultationRepository.save(consultation);

        return OrdonnanceDTO.from(ordonnanceRepository.findById(savedOrdonnance.getId()).orElse(savedOrdonnance));
    }

    @Override
    @Transactional
    public ConsultationDTO updateConsultation(Long id, ConsultationDTO dto) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + id));
        consultation.setCompteRendu(dto.getCompteRendu());
        consultation.setObjectifsMois(dto.getObjectifsMois());
        consultation.setType(dto.getType());
        consultation.setResumeIA(dto.getResumeIA());
        return mapToDTO(consultationRepository.save(consultation));
    }

    @Override
    @Transactional
    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new RuntimeException("Consultation non trouvée: " + id);
        }
        consultationRepository.deleteById(id);
    }

    @Override
    public List<ConsultationDTO> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId)
            .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public ConsultationDTO getDerniereConsultation(Long patientId) {
        return consultationRepository.findByPatientId(patientId)
            .stream()
            .max((a, b) -> a.getDateConsultation().compareTo(b.getDateConsultation()))
            .map(this::mapToDTO)
            .orElse(null);
    }

    private String calculerNiveauAlerte(String type, Double valeur, Long patientId) {
        if (valeur == null) return "NORMAL";
        switch (type) {
            case "GLYCEMIE":
                if (valeur > 2.0) return "CRITIQUE";
                if (valeur > 1.4) return "ATTENTION";
                break;
            case "TENSION_ARTERIELLE":
                if (valeur > 160) return "CRITIQUE";
                if (valeur > 140) return "ATTENTION";
                break;
            case "FREQUENCE_CARDIAQUE":
                if (valeur > 120 || valeur < 50) return "CRITIQUE";
                if (valeur > 100 || valeur < 60) return "ATTENTION";
                break;
            case "SPO2":
                if (valeur < 90) return "CRITIQUE";
                if (valeur < 95) return "ATTENTION";
                break;
        }
        return "NORMAL";
    }

    private ConsultationDTO mapToDTO(Consultation c) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setId(c.getId());
        dto.setPatientId(c.getPatientId());
        dto.setMedecinId(c.getMedecinId());
        dto.setMedecinNom(c.getMedecinNom());
        dto.setDateConsultation(c.getDateConsultation());
        dto.setCompteRendu(c.getCompteRendu());
        dto.setObjectifsMois(c.getObjectifsMois());
        dto.setType(c.getType());
        dto.setResumeIA(c.getResumeIA());
        if (c.getMesures() != null) {
            dto.setMesures(c.getMesures().stream().map(MesureDTO::from).collect(Collectors.toList()));
        }
        if (c.getOrdonnance() != null) {
            dto.setOrdonnance(OrdonnanceDTO.from(c.getOrdonnance()));
        }
        return dto;
    }
}