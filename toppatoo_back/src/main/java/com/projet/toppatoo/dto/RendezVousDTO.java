package com.projet.toppatoo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendezVousDTO {
    private Long id;
    private String patientId;
    private String patientNom;
    private String patientPrenom;
    private String medecinId;
    private String medecinNom;
    private LocalDateTime dateHeure;
    private String motif;
    private String lieu;
    private String statut; // PLANIFIE, CONFIRME, ANNULE, TERMINE
    private Boolean teleconsultation;
    private String lienVisio;
}