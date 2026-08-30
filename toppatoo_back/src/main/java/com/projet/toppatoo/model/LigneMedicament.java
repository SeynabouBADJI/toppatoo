package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ligne_medicaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneMedicament {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "nom_medicament")
    private String nomMedicament;
    
    private String dosage;
    private String posologie;
    
    @Column(name = "duree_jours")
    private Integer dureeJours;
    
    @Column(name = "heures_rappel")
    private String heuresRappel;
    
    private String instructions;
    
    @Column(name = "ordonnance_id")
    private Long ordonnanceId;  // ✅ La propriété utilisée dans mappedBy
}