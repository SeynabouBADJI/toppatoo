package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.LigneMedicamentDTO;
import com.projet.toppatoo.dto.OrdonnanceDTO;
import com.projet.toppatoo.model.LigneMedicament;
import com.projet.toppatoo.model.Ordonnance;
import com.projet.toppatoo.repository.LigneMedicamentRepository;
import com.projet.toppatoo.repository.OrdonnanceRepository;
import com.projet.toppatoo.service.OrdonnanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdonnanceServiceImpl implements OrdonnanceService {

    private final OrdonnanceRepository ordonnanceRepository;
    private final LigneMedicamentRepository ligneMedicamentRepository;

    @Override
    @Transactional
    public OrdonnanceDTO createOrdonnance(Long consultationId, OrdonnanceDTO dto) {
        System.out.println("💊 Création ordonnance pour consultation: " + consultationId);

        // 1️⃣ Créer l'ordonnance
        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setConsultationId(consultationId);
        ordonnance.setDateEmission(LocalDate.now());
        ordonnance.setDateExpiration(
            dto.getDateExpiration() != null ? dto.getDateExpiration() : LocalDate.now().plusMonths(1)
        );
        ordonnance.setNotes(dto.getNotes());

        Ordonnance savedOrdonnance = ordonnanceRepository.save(ordonnance);

        // 2️⃣ Créer les lignes de médicaments
        List<LigneMedicament> medicamentsSauvegardes = new ArrayList<>();
        
        if (dto.getMedicaments() != null && !dto.getMedicaments().isEmpty()) {
            for (LigneMedicamentDTO medDTO : dto.getMedicaments()) {
                LigneMedicament medic = new LigneMedicament();
                medic.setNomMedicament(medDTO.getNomMedicament());
                medic.setDosage(medDTO.getDosage());
                medic.setPosologie(medDTO.getPosologie());
                medic.setDureeJours(medDTO.getDureeJours());
                medic.setHeuresRappel(medDTO.getHeuresRappel());
                medic.setInstructions(medDTO.getInstructions());
                medic.setOrdonnanceId(savedOrdonnance.getId());
                
                medicamentsSauvegardes.add(ligneMedicamentRepository.save(medic));
            }
            System.out.println("✅ " + medicamentsSauvegardes.size() + " médicaments ajoutés");
        }

        // 3️⃣ Mettre à jour l'objet ordonnance avec la liste
        savedOrdonnance.setMedicaments(medicamentsSauvegardes);

        return mapToDTO(savedOrdonnance);
    }

    @Override
    public OrdonnanceDTO getByConsultation(Long consultationId) {
        Ordonnance ordonnance = ordonnanceRepository.findByConsultationId(consultationId)
                .orElseThrow(() -> new RuntimeException("Ordonnance non trouvée pour consultation: " + consultationId));
        return mapToDTO(ordonnance);
    }

    @Override
    public OrdonnanceDTO getById(Long id) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordonnance non trouvée: " + id));
        return mapToDTO(ordonnance);
    }

    @Override
    @Transactional
    public void deleteOrdonnance(Long id) {
        Ordonnance ordonnance = ordonnanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordonnance non trouvée: " + id));
        ordonnanceRepository.delete(ordonnance);
    }

    // ─── MAPPING ─────────────────────────────────────────────────
    private OrdonnanceDTO mapToDTO(Ordonnance ordonnance) {
        OrdonnanceDTO dto = new OrdonnanceDTO();
        dto.setId(ordonnance.getId());
        dto.setConsultationId(ordonnance.getConsultationId());
        dto.setDateEmission(ordonnance.getDateEmission());
        dto.setDateExpiration(ordonnance.getDateExpiration());
        dto.setNotes(ordonnance.getNotes());

        // Charger les médicaments
        List<LigneMedicament> medicaments = ordonnance.getMedicaments();
        
        // Si la liste est null (chargement lazy), faire une requête
        if (medicaments == null) {
            medicaments = ligneMedicamentRepository.findByOrdonnanceId(ordonnance.getId());
        }

        List<LigneMedicamentDTO> medicamentsDTO = medicaments.stream()
                .map(this::mapLigneToDTO)
                .collect(Collectors.toList());

        dto.setMedicaments(medicamentsDTO);
        return dto;
    }

    private LigneMedicamentDTO mapLigneToDTO(LigneMedicament m) {
        LigneMedicamentDTO mDto = new LigneMedicamentDTO();
        mDto.setId(m.getId());
        mDto.setNomMedicament(m.getNomMedicament());
        mDto.setDosage(m.getDosage());
        mDto.setPosologie(m.getPosologie());
        mDto.setDureeJours(m.getDureeJours());
        mDto.setHeuresRappel(m.getHeuresRappel());
        mDto.setInstructions(m.getInstructions());
        return mDto;
    }
}