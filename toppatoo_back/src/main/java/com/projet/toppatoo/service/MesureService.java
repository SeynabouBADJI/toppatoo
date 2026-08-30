package com.projet.toppatoo.service;

import com.projet.toppatoo.dto.MesureDTO;
import java.util.List;

public interface MesureService {
    MesureDTO getMesureById(Long id);
    MesureDTO updateMesure(Long id, MesureDTO mesureDTO);
    void deleteMesure(Long id);
    List<MesureDTO> getMesuresByPatient(Long patientId, String type);
    List<MesureDTO> getMesuresAlerte(Long patientId);
    MesureDTO createMesure(MesureDTO mesureDTO);
    List<MesureDTO> getDernieresMesures(Long patientId, int limit);
}