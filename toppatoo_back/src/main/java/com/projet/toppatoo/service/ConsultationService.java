package com.projet.toppatoo.service;

import com.projet.toppatoo.dto.*;
import java.util.List;

public interface ConsultationService {
    ConsultationDTO getConsultationById(Long id);
    ConsultationDTO creerConsultation(ConsultationCreateDTO request);
    ConsultationDTO updateConsultation(Long id, ConsultationDTO consultationDTO);
    void deleteConsultation(Long id);
    List<ConsultationDTO> getConsultationsByPatient(Long patientId);
    ConsultationDTO getDerniereConsultation(Long patientId);
    MesureDTO ajouterMesure(Long consultationId, MesureCreateDTO request);
    OrdonnanceDTO ajouterOrdonnance(Long consultationId, OrdonnanceCreateDTO request);
}