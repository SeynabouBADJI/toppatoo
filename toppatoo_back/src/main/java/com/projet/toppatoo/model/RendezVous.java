package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "rendez_vous")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id")
    private Long patientId;
    
    @Column(name = "medecin_id")
    private Long medecinId;
    
    @Column(name = "medecin_nom")
    private String medecinNom;
    
    @Column(name = "date_heure")
    private LocalDateTime dateHeure;
    
    private String motif;
    private String lieu;
    
    @Builder.Default
    private String statut = "PLANIFIE";
    
    @Builder.Default
    private Boolean teleconsultation = false;
    
    @Column(name = "lien_visio")
    private String lienVisio;
}