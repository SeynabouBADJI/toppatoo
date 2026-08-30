package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "ordonnances")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ordonnance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "consultation_id")
    private Long consultationId;
    
    @Column(name = "date_emission")
    private LocalDate dateEmission;
    
    @Column(name = "date_expiration")
    private LocalDate dateExpiration;
    
    private String notes;
    
    @OneToMany(mappedBy = "ordonnanceId", cascade = CascadeType.ALL)
    private List<LigneMedicament> medicaments;
}