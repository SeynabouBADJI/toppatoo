package com.projet.toppatoo.controller;

import com.projet.toppatoo.dto.NotificationDTO;
import com.projet.toppatoo.model.Notification;
import com.projet.toppatoo.repository.NotificationRepository;
import com.projet.toppatoo.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.getNotificationsByPatient(patientId));
    }

    @GetMapping("/patient/{patientId}/non-lues")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<List<NotificationDTO>> getNotificationsNonLues(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(patientId));
    }

    @GetMapping("/patient/{patientId}/compteur")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<Long> compterNonLues(@PathVariable Long patientId) {
        return ResponseEntity.ok(notificationService.compterNonLues(patientId));
    }

    @PutMapping("/{id}/lue")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<Void> marquerLue(@PathVariable Long id) {
        notificationService.marquerLue(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Nouvelle méthode : Créer une notification
    @PostMapping
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) {
        return ResponseEntity.ok(notificationService.createNotification(notificationDTO));
    }

    // ✅ Nouvelle méthode : Créer une notification pour un patient
    @PostMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<NotificationDTO> createNotificationForPatient(
            @PathVariable Long patientId,
            @Valid @RequestBody NotificationDTO notificationDTO
    ) {
        return ResponseEntity.ok(notificationService.createNotificationForPatient(patientId, notificationDTO));
    }

    // ✅ Nouvelle méthode : Marquer toutes les notifications d'un patient comme lues
    @PutMapping("/patient/{patientId}/marquer-tout-lu")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<Void> marquerToutLu(@PathVariable Long patientId) {
        notificationService.marquerToutLu(patientId);
        return ResponseEntity.ok().build();
    }

    // ✅ Nouvelle méthode : Récupérer une notification par ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'MEDECIN', 'ADMIN')")
    public ResponseEntity<NotificationDTO> getNotificationById(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.getNotificationById(id));
    }
}