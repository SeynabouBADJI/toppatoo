package com.projet.toppatoo.dto;

import com.projet.toppatoo.model.Mesure;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MesureDTO {
    private Long id;
    private String type;
    private Double valeur;
    private Double valeur2;
    private String unite;
    private String niveauAlerte;
    private LocalDateTime dateMesure;
    private String notes;
    private Long patientId;
    private Long consultationId;

    public static MesureDTO from(Mesure m) {
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
}