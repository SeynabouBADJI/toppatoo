package com.projet.toppatoo.dto;

import com.projet.toppatoo.model.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String role;
    private Boolean actif;

    public static UserDTO from(User u) {
        if (u == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setNom(u.getNom());
        dto.setPrenom(u.getPrenom());
        dto.setEmail(u.getEmail());
        dto.setTelephone(u.getTelephone());
        dto.setRole(u.getRole());
        dto.setActif(u.getActif());
        return dto;
    }
}