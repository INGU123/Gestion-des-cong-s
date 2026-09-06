package com.fruvio.GestionConge.notifications_conge.controller;

import java.sql.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fruvio.GestionConge.notifications_conge.entity.Notification;
import com.fruvio.GestionConge.notifications_conge.repository.NotificationRepository;

@RestController
public class Notification_congeController {
    private final NotificationRepository notificationRepository;

    public Notification_congeController(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @GetMapping("/notification")
    public String get(@RequestParam Long id, @RequestParam Long utilisateurId, @RequestParam String contenu,
            @RequestParam String type, @RequestParam boolean lue, @RequestParam Date date_envoi,
            @RequestParam Date date_lecture, @RequestParam String lien) {
        Notification notif = Notification.builder().id(id).utilisateurId(utilisateurId).contenu(contenu).type(type)
                .lue(false).date_envoi(date_envoi).date_lecture(date_lecture).lien(lien).build();

        notificationRepository.save(notif);

        return "Notificaiton";
    }
}
