package com.projet.toppatoo.service;

import com.projet.toppatoo.dto.ConsultationDTO;
import java.util.List;

public interface ConsultationService {
    ConsultationDTO getConsultationById(Long id);
    ConsultationDTO updateConsultation(Long id, ConsultationDTO consultationDTO);
    void deleteConsultation(Long id);
    List<ConsultationDTO> getConsultationsByPatient(Long patientId);
}