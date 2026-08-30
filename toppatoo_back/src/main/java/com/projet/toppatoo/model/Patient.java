package com.projet.toppatoo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "medecin_id")
    private Long medecinId;

    @Column(name = "medecin_nom")
    private String medecinNom;

    @Column(name = "date_naissance")
    private LocalDate dateNaissance;

    private String sexe;

    @Column(name = "groupe_sanguin")
    private String groupeSanguin;

    private String adresse;
    private String ville;
    private String antecedents;
    private String allergies;

    @Column(name = "maladie_chronique")
    private String maladieChronique;

    @Column(name = "seuil_glycemie_min")
    private Double seuilGlycemieMin;

    @Column(name = "seuil_glycemie_max")
    private Double seuilGlycemieMax;

    @Column(name = "seuil_tension_syst_max")
    private Double seuilTensionSystMax;

    @Column(name = "seuil_tension_diast_max")
    private Double seuilTensionDiastMax;

    @Column(name = "seuil_poids_min")
    private Double seuilPoidsMin;

    @Column(name = "seuil_poids_max")
    private Double seuilPoidsMax;
}