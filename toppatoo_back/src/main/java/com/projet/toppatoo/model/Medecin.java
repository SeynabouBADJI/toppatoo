package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medecins")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medecin {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private String specialite;
    
    @Column(name = "numero_rpps")
    private String numeroRpps;
    
    @Column(name = "adresse_cabinet")
    private String adresseCabinet;
    
    @Column(name = "telephone_cabinet")
    private String telephoneCabinet;
    
    private String ville;
    private String etablissement;  // ✅ Ajouté
}