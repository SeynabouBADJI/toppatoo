package com.projet.toppatoo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceDTO {
    private Long id;
    private String consultationId;
    private LocalDate dateEmission;
    private LocalDate dateExpiration;
    private String notes;
    private List<LigneMedicamentDTO> medicaments;
}