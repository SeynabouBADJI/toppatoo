package com.projet.toppatoo.service.impl;

import com.projet.toppatoo.dto.NotificationDTO;
import com.projet.toppatoo.model.Notification;
import com.projet.toppatoo.model.Patient;
import com.projet.toppatoo.model.RendezVous;
import com.projet.toppatoo.repository.NotificationRepository;
import com.projet.toppatoo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<NotificationDTO> getNotificationsByPatient(Long patientId) {
        return notificationRepository.findByPatientId(patientId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> getNotificationsNonLues(Long patientId) {
        return notificationRepository.findByPatientIdAndLueFalse(patientId)
            .stream()
            .map(this::mapToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public Long compterNonLues(Long patientId) {
        return notificationRepository.countByPatientIdAndLueFalse(patientId);
    }

    @Override
    @Transactional
    public void marquerLue(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID: " + id));
        notification.setLue(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        if (!notificationRepository.existsById(id)) {
            throw new RuntimeException("Notification non trouvée avec l'ID: " + id);
        }
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public NotificationDTO createNotification(NotificationDTO notificationDTO) {
        Notification notification = mapToEntity(notificationDTO);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setLue(false);
        Notification saved = notificationRepository.save(notification);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public NotificationDTO createNotificationForPatient(Long patientId, NotificationDTO notificationDTO) {
        Notification notification = mapToEntity(notificationDTO);
        notification.setPatientId(patientId);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setLue(false);
        Notification saved = notificationRepository.save(notification);
        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public void marquerToutLu(Long patientId) {
        List<Notification> nonLues = notificationRepository.findByPatientIdAndLueFalse(patientId);
        nonLues.forEach(notif -> notif.setLue(true));
        notificationRepository.saveAll(nonLues);
    }

    public void notifierNouveauRdv(Patient patient, RendezVous rendezVous) {
        Notification notification = Notification.builder()
                .patientId(patient.getId())
                .titre("Nouveau rendez-vous")
                .message("Votre rendez-vous avec " + rendezVous.getMedecinNom() + 
                         " est programmé le " + rendezVous.getDateHeure())
                .type("RAPPEL_RENDEZVOUS")
                .lue(false)
                .build();
        notificationRepository.save(notification);
    }

    @Override
    public NotificationDTO getNotificationById(Long id) {
        Notification notification = notificationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Notification non trouvée avec l'ID: " + id));
        return mapToDTO(notification);
    }

    // Mapping Notification -> NotificationDTO
    private NotificationDTO mapToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setTitre(notification.getTitre());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setPatientId(notification.getPatientId());
        dto.setLue(notification.getLue());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setNiveauAlerte(notification.getNiveauAlerte());
        return dto;
    }

    // Mapping NotificationDTO -> Notification
    private Notification mapToEntity(NotificationDTO dto) {
        Notification notification = new Notification();
        notification.setTitre(dto.getTitre());
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setPatientId(dto.getPatientId());
        notification.setLue(dto.getLue() != null ? dto.getLue() : false);
        notification.setNiveauAlerte(dto.getNiveauAlerte());
        notification.setCreatedAt(LocalDateTime.now());
        return notification;
    }
}