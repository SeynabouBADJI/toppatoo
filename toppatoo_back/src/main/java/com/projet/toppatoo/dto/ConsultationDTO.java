package com.projet.toppatoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConsultationDTO {
    private Long id;
    private Long patientId;
    private String patientNom;
    private Long medecinId;
    private String medecinNom;
    private LocalDateTime dateConsultation;
    private String compteRendu;
    private String objectifsMois;
    private String type;
    private String resumeIA;
    private List<MesureDTO> mesures;
    private OrdonnanceDTO ordonnance;
}