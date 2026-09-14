package com.projet.toppatoo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrdonnanceDTO {
    private Long id;
    private Long consultationId;
    private LocalDate dateEmission;
    private LocalDate dateExpiration;
    private String notes;
    private List<LigneMedicamentDTO> medicaments;
}