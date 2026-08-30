
package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.ConsultationDTO;
import com.projet.toppatoo.model.Consultation;
import com.projet.toppatoo.repository.ConsultationRepository;
import com.projet.toppatoo.service.ConsultationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

    private final ConsultationRepository consultationRepository;

    @Override
    public ConsultationDTO getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée avec l'ID: " + id));
        return mapToDTO(consultation);
    }

    @Override
    @Transactional
    public ConsultationDTO updateConsultation(Long id, ConsultationDTO consultationDTO) {
        Consultation consultation = consultationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Consultation non trouvée avec l'ID: " + id));
        
        // Mise à jour des champs
        consultation.setCompteRendu(consultationDTO.getCompteRendu());
        consultation.setObjectifsMois(consultationDTO.getObjectifsMois());
        consultation.setType(consultationDTO.getType());
        consultation.setResumeIA(consultationDTO.getResumeIA());
        
        Consultation updated = consultationRepository.save(consultation);
        return mapToDTO(updated);
    }

    @Override
    @Transactional
    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new RuntimeException("Consultation non trouvée avec l'ID: " + id);
        }
        consultationRepository.deleteById(id);
    }

    @Override
    public List<ConsultationDTO> getConsultationsByPatient(Long patientId) {
        return consultationRepository.findByPatientId(patientId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    // Méthode de mapping Consultation -> ConsultationDTO
    private ConsultationDTO mapToDTO(Consultation consultation) {
        ConsultationDTO dto = new ConsultationDTO();
        dto.setId(consultation.getId());
        dto.setPatientId(consultation.getPatientId());
        dto.setMedecinId(consultation.getMedecinId());
        dto.setMedecinNom(consultation.getMedecinNom());
        dto.setDateConsultation(consultation.getDateConsultation());
        dto.setCompteRendu(consultation.getCompteRendu());
        dto.setObjectifsMois(consultation.getObjectifsMois());
        dto.setType(consultation.getType());
        dto.setResumeIA(consultation.getResumeIA());
        return dto;
    }
}
