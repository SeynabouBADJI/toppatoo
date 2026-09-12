package com.projet.toppatoo.dto;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class OrdonnanceCreateDTO {
    private String notes;
    private LocalDate dateExpiration;
    private List<LigneMedicamentDTO> medicaments;
}