package com.projet.toppatoo.dto;

import lombok.Data;

@Data
public class MesureCreateDTO {
    private String type;
    private Double valeur;
    private Double valeur2;
    private String unite;
    private String notes;
}