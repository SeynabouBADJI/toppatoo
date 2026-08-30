package com.projet.toppatoo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneMedicamentDTO {
    private Long id;
    private String nomMedicament;
    private String dosage;
    private String posologie;
    private Integer dureeJours;
    private String heuresRappel;
    private String instructions;
    private String ordonnanceId;
}