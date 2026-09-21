package com.fruvio.GestionConge.notifications_conge.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fruvio.GestionConge.notifications_conge.entity.Notification;
import com.fruvio.GestionConge.notifications_conge.repository.NotificationRepository;
import com.fruvio.GestionConge.notifications_conge.service.Notification_congeService;

@RestController
@RequestMapping("/notification")
public class Notification_congeController {
    private final NotificationRepository notificationRepository;
    private final Notification_congeService notificationService;

    public Notification_congeController(NotificationRepository notificationRepository,
                                        Notification_congeService notificationService) {
        this.notificationRepository = notificationRepository;
        this.notificationService = notificationService;
    }

    // Notifications non lues d'un utilisateur
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/utilisateur/{utilisateurId}/non-lues")
    public ResponseEntity<List<Notification>> getNonLues(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.getNotificationsNonLues(utilisateurId));
    }

    // Toutes les notifications d'un utilisateur
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Notification>> getToutes(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(notificationService.getToutesNotifications(utilisateurId));
    }

    // Marquer comme lue
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @PutMapping("/{notificationId}/lire")
    public ResponseEntity<Void> marquerCommeLue(@PathVariable Long notificationId) {
        notificationService.marquerCommeLue(notificationId);
        return ResponseEntity.ok().build();
    }

    // Créer manuellement une notification (Réservé à l'Admin - méthode POST)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/creer")
    public ResponseEntity<String> creerNotification(@RequestParam(required = false) Long id, 
                                                    @RequestParam Long utilisateurId, 
                                                    @RequestParam String contenu,
                                                    @RequestParam String type, 
                                                    @RequestParam(defaultValue = "false") boolean lue, 
                                                    @RequestParam Date date_envoi,
                                                    @RequestParam(required = false) Date date_lecture, 
                                                    @RequestParam(required = false) String lien) {
        Notification notif = Notification.builder()
                .id(id)
                .utilisateurId(utilisateurId)
                .contenu(contenu)
                .type(type)
                .lue(lue)
                .date_envoi(date_envoi)
                .date_lecture(date_lecture)
                .lien(lien)
                .build();

        notificationRepository.save(notif);
        return ResponseEntity.ok("Notification créée avec succès");
    }
}