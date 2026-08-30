package com.projet.toppatoo.dto;

import com.projet.toppatoo.model.Patient;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PatientDTO {
    private Long id;
    private UserDTO user;
    private Long medecinId;
    private String medecinNom;
    private LocalDate dateNaissance;
    private String sexe;
    private String groupeSanguin;
    private String adresse;
    private String ville;
    private String antecedents;
    private String allergies;
    private String maladieChronique;
    private Double seuilGlycemieMin;
    private Double seuilGlycemieMax;
    private Double seuilTensionSystMax;
    private Double seuilTensionDiastMax;
    private Double seuilPoidsMin;
    private Double seuilPoidsMax;

    public static PatientDTO from(Patient p) {
        PatientDTO dto = new PatientDTO();
        dto.setId(p.getId());
        if (p.getUser() != null) dto.setUser(UserDTO.from(p.getUser()));
        dto.setMedecinId(p.getMedecinId());
        dto.setMedecinNom(p.getMedecinNom());
        dto.setDateNaissance(p.getDateNaissance());
        dto.setSexe(p.getSexe());
        dto.setGroupeSanguin(p.getGroupeSanguin());
        dto.setAdresse(p.getAdresse());
        dto.setVille(p.getVille());
        dto.setAntecedents(p.getAntecedents());
        dto.setAllergies(p.getAllergies());
        dto.setMaladieChronique(p.getMaladieChronique());
        dto.setSeuilGlycemieMin(p.getSeuilGlycemieMin());
        dto.setSeuilGlycemieMax(p.getSeuilGlycemieMax());
        dto.setSeuilTensionSystMax(p.getSeuilTensionSystMax());
        dto.setSeuilTensionDiastMax(p.getSeuilTensionDiastMax());
        dto.setSeuilPoidsMin(p.getSeuilPoidsMin());
        dto.setSeuilPoidsMax(p.getSeuilPoidsMax());
        return dto;
    }
}