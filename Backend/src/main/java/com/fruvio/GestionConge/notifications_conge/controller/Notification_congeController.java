package com.fruvio.GestionConge.notifications_conge.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fruvio.GestionConge.notifications_conge.entity.Notification;
import com.fruvio.GestionConge.notifications_conge.repository.NotificationRepository;
import com.fruvio.GestionConge.notifications_conge.service.Notification_congeService;

@RestController
@RequestMapping("/notification")
@CrossOrigin(origins = "http://localhost:3000")
public class Notification_congeController {
    private final NotificationRepository notificationRepository;
    private final Notification_congeService notificationService;

    public Notification_congeController(NotificationRepository notificationRepository,
                                        Notification_congeService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    // Notifications non lues d'un utilisateur
    @GetMapping({"/utilisateur/{utilisateurId}/non-lues", "/utilisateur{utilisateurId}/non-lues"})
    public ResponseEntity<List<Notification>> getNonLues(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(utilisateurId));
    }

    // Toutes les notifications d'un utilisateur
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Notification>> getToutes(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.getToutesNotifications(utilisateurId));
    }

    // Marquer comme lue
    @PutMapping("/{notificationId}/lire")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable Long notificationId) {
        notificationService.marquerCommeLue(notificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/creer")
    public String get(@RequestParam Long id, @RequestParam Long utilisateurId, @RequestParam String contenu,
            @RequestParam String type, @RequestParam boolean lue, @RequestParam Date date_envoi,
            @RequestParam Date date_lecture, @RequestParam String lien) {
        Notification notif = Notification.builder().id(id).utilisateurId(utilisateurId).contenu(contenu).type(type)
                .lue(false).date_envoi(date_envoi).date_lecture(date_lecture).lien(lien).build();

        notificationRepository.save(notif);
        return "Notificaiton";
    }
}

