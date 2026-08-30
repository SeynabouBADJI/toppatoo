package com.projet.toppatoo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedecinDTO {
    private Long id;
    private UserDTO user;
    private String specialite;
    private String numeroRpps;
    private String adresseCabinet;
    private String telephoneCabinet;
    private List<PatientDTO> patients;
    private List<RendezVousDTO> rendezVous;
}