package com.fruvio.GestionConge.historique_mouvement.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;
import com.fruvio.GestionConge.historique_mouvement.repository.Historique_mouvementRepository;
import com.fruvio.GestionConge.historique_mouvement.service.Historique_mouvementService;

@RestController
@RequestMapping("/historiques")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"}, allowCredentials = "true")
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
        return ResponseEntity.ok(historiqueService.getAllMouvements());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Historique_mouvement>> getParUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(historiqueService.getMouvementsParUtilisateur(utilisateurId));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/creer")
    public String get(@RequestParam Long utilisateurId, @RequestParam String typeMouvement,
            @RequestParam Integer quantite, @RequestParam Long type_conge_id, @RequestParam Long demande_id,
            @RequestParam String commentaire, @RequestParam Long effectue_par, @RequestParam Date date) {
        Historique_mouvement Historique = Historique_mouvement.builder()
                .utilisateurId(utilisateurId)
                .typeMouvement(typeMouvement)
                .quantite(quantite != null ? quantite : 0)
                .type_conge_id(type_conge_id)
                .demande_id(demande_id)
                .commentaire(commentaire)
                .effectue_par(effectue_par)
                .date(date)
                .build();

        historique_mouvementRepository.save(Historique);
        return "historique";
    }
}
