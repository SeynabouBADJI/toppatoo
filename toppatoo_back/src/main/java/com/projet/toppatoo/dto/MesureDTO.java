package com.projet.toppatoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MesureDTO {
    private Long id;
    private String type; // GLYCEMIE, TENSION_ARTERIELLE, POIDS, FREQUENCE_CARDIAQUE
    private Double valeur;
    private Double valeur2; // Pour la tension systolique/diastolique
    private String unite;
    private String niveauAlerte; // CRITIQUE, ATTENTION, NORMAL
    private LocalDateTime dateMesure;
    private String notes;
    private Long patientId;
    private Long consultationId;
}