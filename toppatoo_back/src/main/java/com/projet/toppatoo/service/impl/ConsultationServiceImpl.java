package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.ConsultationCreateDTO;
import com.projet.toppatoo.dto.ConsultationDTO;
import com.projet.toppatoo.dto.LigneMedicamentDTO;
import com.projet.toppatoo.dto.MesureCreateDTO;
import com.projet.toppatoo.dto.MesureDTO;
import com.projet.toppatoo.dto.OrdonnanceCreateDTO;
import com.projet.toppatoo.dto.OrdonnanceDTO;
import com.projet.toppatoo.model.Consultation;
import com.projet.toppatoo.model.LigneMedicament;
import com.projet.toppatoo.model.Mesure;
import com.projet.toppatoo.model.Ordonnance;
import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.repository.ConsultationRepository;
import com.projet.toppatoo.repository.LigneMedicamentRepository;
import com.projet.toppatoo.repository.MesureRepository;
import com.projet.toppatoo.repository.OrdonnanceRepository;
import com.projet.toppatoo.repository.PatientRepository;
import com.projet.toppatoo.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    // ═══════════════════════════════════════════════════════════════
    // CRÉER UNE CONSULTATION
    // ═══════════════════════════════════════════════════════════════
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

    // ═══════════════════════════════════════════════════════════════
    // GET BY ID
    // ═══════════════════════════════════════════════════════════════
    @Override
    public ConsultationDTO getConsultationById(Long id) {
        return mapToDTO(consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + id)));
    }

    // ═══════════════════════════════════════════════════════════════
    // AJOUTER UNE MESURE
    // ═══════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public MesureDTO ajouterMesure(Long consultationId, MesureCreateDTO request) {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + consultationId));

        // Calculer le niveau d'alerte
        String niveauAlerte = calculerNiveauAlerte(
            request.getType(), 
            request.getValeur(), 
            request.getValeur2(),
            consultation.getPatientId()
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

        return mapMesureToDTO(mesureRepository.save(mesure));
    }

    // ═══════════════════════════════════════════════════════════════
    // AJOUTER UNE ORDONNANCE
    // ═══════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public OrdonnanceDTO ajouterOrdonnance(Long consultationId, OrdonnanceCreateDTO request) {
        Consultation consultation = consultationRepository.findById(consultationId)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée: " + consultationId));

        // Créer l'ordonnance
        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setNotes(request.getNotes());
        ordonnance.setConsultationId(consultationId);
        ordonnance.setDateEmission(LocalDate.now());
        ordonnance.setDateExpiration(
            request.getDateExpiration() != null 
                ? request.getDateExpiration() 
                : LocalDate.now().plusMonths(1)
        );

        Ordonnance savedOrdonnance = ordonnanceRepository.save(ordonnance);

        // Créer les lignes de médicaments
        List<LigneMedicament> lignesSauvegardees = new ArrayList<>();

        if (request.getMedicaments() != null && !request.getMedicaments().isEmpty()) {
            for (LigneMedicamentDTO m : request.getMedicaments()) {
                LigneMedicament ligne = new LigneMedicament();
                ligne.setNomMedicament(m.getNomMedicament());
                ligne.setDosage(m.getDosage());
                ligne.setPosologie(m.getPosologie());
                ligne.setDureeJours(m.getDureeJours());
                ligne.setHeuresRappel(m.getHeuresRappel());
                ligne.setInstructions(m.getInstructions());
                ligne.setOrdonnanceId(savedOrdonnance.getId());
                lignesSauvegardees.add(ligneMedicamentRepository.save(ligne));
            }
        }

        savedOrdonnance.setMedicaments(lignesSauvegardees);

        return mapOrdonnanceToDTO(savedOrdonnance);
    }

    // ═══════════════════════════════════════════════════════════════
    // UPDATE CONSULTATION
    // ═══════════════════════════════════════════════════════════════
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

    // ═══════════════════════════════════════════════════════════════
    // DELETE CONSULTATION
    // ═══════════════════════════════════════════════════════════════
    @Override
    @Transactional
    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new RuntimeException("Consultation non trouvée: " + id);
        }
        consultationRepository.deleteById(id);
    }

    // ═══════════════════════════════════════════════════════════════
    // GET BY PATIENT
    // ═══════════════════════════════════════════════════════════════
    @Override
    public List<ConsultationDTO> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // DERNIÈRE CONSULTATION
    // ═══════════════════════════════════════════════════════════════
    @Override
    public ConsultationDTO getDerniereConsultation(Long patientId) {
        return consultationRepository.findByPatientId(patientId)
            .stream()
            .max((a, b) -> {
                if (a.getDateConsultation() == null && b.getDateConsultation() == null) return 0;
                if (a.getDateConsultation() == null) return -1;
                if (b.getDateConsultation() == null) return 1;
                return a.getDateConsultation().compareTo(b.getDateConsultation());
            })
            .map(this::mapToDTO)
            .orElse(null);
    }

    // ═══════════════════════════════════════════════════════════════
    // CALCUL DU NIVEAU D'ALERTE
    // ═══════════════════════════════════════════════════════════════
    private String calculerNiveauAlerte(String type, Double valeur, Double valeur2, Long patientId) {
        if (valeur == null || type == null) return "NORMAL";

        // Récupérer les seuils du patient si disponibles
        Patient patient = patientRepository.findById(patientId).orElse(null);

        switch (type.toUpperCase()) {
            case "GLYCEMIE":
                double glyMin = patient != null && patient.getSeuilGlycemieMin() != null
                    ? patient.getSeuilGlycemieMin() : 0.7;
                double glyMax = patient != null && patient.getSeuilGlycemieMax() != null
                    ? patient.getSeuilGlycemieMax() : 1.1;
                
                if (valeur < 0.5 || valeur > 2.0) return "CRITIQUE";
                if (valeur < glyMin || valeur > glyMax) return "ATTENTION";
                break;

            case "TENSION_ARTERIELLE":
                double syst = valeur;
                double diast = valeur2 != null ? valeur2 : 0;
                
                if (syst >= 180 || diast >= 120 || syst < 90 || (diast > 0 && diast < 60)) return "CRITIQUE";
                if (syst >= 140 || diast >= 90 || syst >= 130 || diast >= 85) return "ATTENTION";
                break;

            case "TEMPERATURE":
                if (valeur < 35.0 || valeur >= 40.0) return "CRITIQUE";
                if (valeur < 36.0 || valeur >= 38.0) return "ATTENTION";
                break;

            case "POIDS":
                if (patient != null) {
                    if (patient.getSeuilPoidsMin() != null && valeur < patient.getSeuilPoidsMin()) return "ATTENTION";
                    if (patient.getSeuilPoidsMax() != null && valeur > patient.getSeuilPoidsMax()) return "ATTENTION";
                }
                break;

            case "FREQUENCE_CARDIAQUE":
                if (valeur < 40 || valeur > 150) return "CRITIQUE";
                if (valeur < 60 || valeur > 100) return "ATTENTION";
                break;
        }
        return "NORMAL";
    }

    // ═══════════════════════════════════════════════════════════════
    // MAPPING
    // ═══════════════════════════════════════════════════════════════
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

        // Charger les mesures
        List<Mesure> mesures = mesureRepository.findByConsultationId(c.getId());
        if (mesures != null) {
            dto.setMesures(mesures.stream()
                .map(this::mapMesureToDTO)
                .collect(Collectors.toList()));
        }

        // Charger l'ordonnance
        Ordonnance ordonnance = ordonnanceRepository.findByConsultationId(c.getId()).orElse(null);
        if (ordonnance != null) {
            dto.setOrdonnance(mapOrdonnanceToDTO(ordonnance));
        }

        return dto;
    }

    private MesureDTO mapMesureToDTO(Mesure m) {
        MesureDTO dto = new MesureDTO();
        dto.setId(m.getId());
        dto.setType(m.getType());
        dto.setValeur(m.getValeur());
        dto.setValeur2(m.getValeur2());
        dto.setUnite(m.getUnite());
        dto.setNiveauAlerte(m.getNiveauAlerte());
        dto.setDateMesure(m.getDateMesure());
        dto.setNotes(m.getNotes());
        dto.setPatientId(m.getPatientId());
        dto.setConsultationId(m.getConsultationId());
        return dto;
    }

    private OrdonnanceDTO mapOrdonnanceToDTO(Ordonnance o) {
        OrdonnanceDTO dto = new OrdonnanceDTO();
        dto.setId(o.getId());
        dto.setConsultationId(o.getConsultationId());
        dto.setDateEmission(o.getDateEmission());
        dto.setDateExpiration(o.getDateExpiration());
        dto.setNotes(o.getNotes());

        // Charger les médicaments
        List<LigneMedicament> medicaments = o.getMedicaments();
        if (medicaments == null) {
            medicaments = ligneMedicamentRepository.findByOrdonnanceId(o.getId());
        }

        if (medicaments != null) {
            dto.setMedicaments(medicaments.stream()
                .map(this::mapLigneToDTO)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    private LigneMedicamentDTO mapLigneToDTO(LigneMedicament m) {
        LigneMedicamentDTO dto = new LigneMedicamentDTO();
        dto.setId(m.getId());
        dto.setNomMedicament(m.getNomMedicament());
        dto.setDosage(m.getDosage());
        dto.setPosologie(m.getPosologie());
        dto.setDureeJours(m.getDureeJours());
        dto.setHeuresRappel(m.getHeuresRappel());
        dto.setInstructions(m.getInstructions());
        return dto;
    }
}