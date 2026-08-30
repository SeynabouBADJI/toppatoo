package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "consultations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Consultation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "patient_id")
    private Long patientId;  // ✅ La propriété utilisée dans mappedBy
    
    @Column(name = "medecin_id")
    private Long medecinId;
    
    @Column(name = "medecin_nom")
    private String medecinNom;
    
    @Column(name = "date_consultation")
    private LocalDateTime dateConsultation;
    
    @Column(name = "compte_rendu", columnDefinition = "TEXT")
    private String compteRendu;
    
    @Column(name = "objectifs_mois")
    private String objectifsMois;
    
    private String type;
    
    @Column(name = "resume_ia", columnDefinition = "TEXT")
    private String resumeIA;
    
    @OneToMany(mappedBy = "consultationId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Mesure> mesures;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ordonnance_id")
    private Ordonnance ordonnance;
}