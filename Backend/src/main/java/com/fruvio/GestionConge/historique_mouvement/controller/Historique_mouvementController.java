package com.fruvio.GestionConge.historique_mouvement.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;
import com.fruvio.GestionConge.historique_mouvement.repository.Historique_mouvementRepository;
import com.fruvio.GestionConge.historique_mouvement.service.Historique_mouvementService;
import com.fruvio.GestionConge.utilisateur.config.CustomUserDetails;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;

@RestController
@RequestMapping("/historiques")
public class Historique_mouvementController {

    private final Historique_mouvementRepository historique_mouvementRepository;
    private final Historique_mouvementService historiqueService;

    public Historique_mouvementController(Historique_mouvementRepository historique_mouvementRepository,
                                          Historique_mouvementService historiqueService) {
        this.historique_mouvementRepository = historique_mouvementRepository;
        this.historiqueService = historiqueService;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping({"", "/all"})
    public ResponseEntity<List<Historique_mouvement>> getAll() {
        Utilisateur currentUser = getCurrentAuthenticatedUser();
        return ResponseEntity.ok(historiqueService.getAllMouvements(currentUser));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Historique_mouvement>> getParUtilisateur(@PathVariable Long utilisateurId) {
        Utilisateur currentUser = getCurrentAuthenticatedUser();
        return ResponseEntity.ok(historiqueService.getMouvementsParUtilisateur(utilisateurId, currentUser));
    }

    // Création d'un mouvement d'historique (Strictement POST et ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/creer")
    public ResponseEntity<String> creerMouvement(
            @RequestBody(required = false) Historique_mouvement body,
            @RequestParam(required = false) Long utilisateurId,
            @RequestParam(required = false) String typeMouvement,
            @RequestParam(required = false) Integer quantite,
            @RequestParam(required = false) Long type_conge_id,
            @RequestParam(required = false) Long demande_id,
            @RequestParam(required = false) String commentaire,
            @RequestParam(required = false) Long effectue_par,
            @RequestParam(required = false) Date date) {

        Long targetUserId = body != null && body.getUtilisateurId() != null ? body.getUtilisateurId() : utilisateurId;
        String targetType = body != null && body.getTypeMouvement() != null ? body.getTypeMouvement() : typeMouvement;
        Integer targetQuantite = body != null && body.getQuantite() != null ? body.getQuantite() : (quantite != null ? quantite : 0);
        Long targetTypeCongeId = body != null && body.getType_conge_id() != null ? body.getType_conge_id() : type_conge_id;
        Long targetDemandeId = body != null && body.getDemande_id() != null ? body.getDemande_id() : demande_id;
        String targetCommentaire = body != null && body.getCommentaire() != null ? body.getCommentaire() : commentaire;
        Long targetEffectuePar = body != null && body.getEffectue_par() != null ? body.getEffectue_par() : effectue_par;
        Date targetDate = body != null && body.getDate() != null ? body.getDate() : (date != null ? date : new Date(System.currentTimeMillis()));

        if (targetUserId == null || targetType == null) {
            return ResponseEntity.badRequest().body("L'identifiant utilisateur et le type de mouvement sont obligatoires.");
        }

        Historique_mouvement mouvement = Historique_mouvement.builder()
                .utilisateurId(targetUserId)
                .typeMouvement(targetType)
                .quantite(targetQuantite)
                .type_conge_id(targetTypeCongeId)
                .demande_id(targetDemandeId)
                .commentaire(targetCommentaire)
                .effectue_par(targetEffectuePar)
                .date(targetDate)
                .build();

        historique_mouvementRepository.save(mouvement);
        return ResponseEntity.status(HttpStatus.CREATED).body("Mouvement d'historique enregistré avec succès.");
    }

    private Utilisateur getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        Object principal = auth.getPrincipal();
        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUtilisateur();
        }
        if (principal instanceof Utilisateur utilisateur) {
            return utilisateur;
        }

        return null;
    }
}
