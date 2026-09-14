package com.projet.toppatoo.service;

import com.projet.toppatoo.dto.OrdonnanceDTO;

public interface OrdonnanceService {
    OrdonnanceDTO createOrdonnance(Long consultationId, OrdonnanceDTO ordonnanceDTO);
    OrdonnanceDTO getByConsultation(Long consultationId);
    OrdonnanceDTO getById(Long id);
    void deleteOrdonnance(Long id);
}