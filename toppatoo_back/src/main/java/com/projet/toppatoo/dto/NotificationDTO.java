package com.projet.toppatoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private String titre;
    private String message;
    private String type; // ALERTE_MESURE, RAPPEL_MEDICAMENT, RAPPEL_RENDEZVOUS, INFO
    private Long patientId;
    private Boolean lue;
    private LocalDateTime createdAt;
    private String niveauAlerte; // CRITIQUE, ATTENTION, INFO
}