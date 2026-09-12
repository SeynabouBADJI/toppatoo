package com.projet.toppatoo.dto;

import lombok.Data;

@Data
public class ConsultationCreateDTO {
    private Long patientId;
    private Long medecinId;
    private String compteRendu;
    private String objectifsMois;
    private String type;
}