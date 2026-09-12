package com.projet.toppatoo.dto;

import com.projet.toppatoo.model.Ordonnance;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class OrdonnanceDTO {
    private Long id;
    private String notes;
    private LocalDate dateExpiration;
    private List<LigneMedicamentDTO> medicaments;

    public static OrdonnanceDTO from(Ordonnance o) {
        if (o == null) return null;
        OrdonnanceDTO dto = new OrdonnanceDTO();
        dto.setId(o.getId());
        dto.setNotes(o.getNotes());
        dto.setDateExpiration(o.getDateExpiration());
        if (o.getMedicaments() != null) {
            dto.setMedicaments(o.getMedicaments().stream().map(m -> {
                LigneMedicamentDTO l = new LigneMedicamentDTO();
                l.setId(m.getId());
                l.setNomMedicament(m.getNomMedicament());
                l.setDosage(m.getDosage());
                l.setPosologie(m.getPosologie());
                l.setDureeJours(m.getDureeJours());
                l.setHeuresRappel(m.getHeuresRappel());
                l.setInstructions(m.getInstructions());
                return l;
            }).collect(Collectors.toList()));
        }
        return dto;
    }
}