package com.projet.toppatoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequestDTO {
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private String password; // Optionnel - pour changer le mot de passe
    private Boolean actif;
}