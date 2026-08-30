package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.MesureDTO;
import com.projet.toppatoo.model.Mesure;
import com.projet.toppatoo.repository.MesureRepository;
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

    @Override
    public MesureDTO getMesureById(Long id) {
        Mesure mesure = mesureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesure non trouvée avec l'ID: " + id));
        return mapToDTO(mesure);
    }

    @Override
    @Transactional
    public MesureDTO updateMesure(Long id, MesureDTO mesureDTO) {
        Mesure mesure = mesureRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mesure non trouvée avec l'ID: " + id));
        
        // Mise à jour des champs
        mesure.setValeur(mesureDTO.getValeur());
        mesure.setValeur2(mesureDTO.getValeur2());
        mesure.setUnite(mesureDTO.getUnite());
        mesure.setNiveauAlerte(mesureDTO.getNiveauAlerte());
        mesure.setNotes(mesureDTO.getNotes());
        
        Mesure updated = mesureRepository.save(mesure);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteMesure(Long id) {
        if (!mesureRepository.existsById(id)) {
            throw new RuntimeException("Mesure non trouvée avec l'ID: " + id);
        }
        mesureRepository.deleteById(id);
    }

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

    @Override
    public List<MesureDTO> getMesuresAlerte(Long patientId) {
        List<String> niveauxAlerte = Arrays.asList("CRITIQUE", "ATTENTION");
        return mesureRepository.findByPatientIdAndNiveauAlerteIn(patientId, niveauxAlerte)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MesureDTO createMesure(MesureDTO mesureDTO) {
        Mesure mesure = mapToEntity(mesureDTO);
        mesure.setDateMesure(LocalDateTime.now());
        Mesure saved = mesureRepository.save(mesure);
        return mapToDTO(saved);
    }

    @Override
    public List<MesureDTO> getDernieresMesures(Long patientId, int limit) {
        // Récupérer les 10 dernières mesures
        List<Mesure> mesures = mesureRepository.findByPatientId(patientId);
        
        // Trier par date décroissante et limiter
        return mesures.stream()
            .sorted((m1, m2) -> m2.getDateMesure().compareTo(m1.getDateMesure()))
            .limit(limit)
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // Mapping Mesure -> MesureDTO
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

    // Mapping MesureDTO -> Mesure
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