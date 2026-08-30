package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "mesures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Mesure {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private Double valeur;
    
    private Double valeur2;
    
    @Column(nullable = false)
    private String unite;
    
    @Column(name = "niveau_alerte")
    private String niveauAlerte;
    
    @Column(name = "date_mesure")
    private LocalDateTime dateMesure;
    
    private String notes;
    
    @Column(name = "patient_id")
    private Long patientId;
    
    @Column(name = "consultation_id")
    private Long consultationId;  // ✅ La propriété utilisée dans mappedBy
}