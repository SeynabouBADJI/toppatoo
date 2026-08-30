package com.projet.toppatoo.service;

import com.projet.toppatoo.dto.NotificationDTO;
import java.util.List;

public interface NotificationService {
    List<NotificationDTO> getNotificationsByPatient(Long patientId);
    List<NotificationDTO> getNotificationsNonLues(Long patientId);
    Long compterNonLues(Long patientId);
    void marquerLue(Long id);
    void deleteNotification(Long id);
    NotificationDTO createNotification(NotificationDTO notificationDTO);
    NotificationDTO createNotificationForPatient(Long patientId, NotificationDTO notificationDTO);
    void marquerToutLu(Long patientId);
    NotificationDTO getNotificationById(Long id);
}