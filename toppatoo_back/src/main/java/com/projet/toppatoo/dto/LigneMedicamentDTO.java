package com.projet.toppatoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}