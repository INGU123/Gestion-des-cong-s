package com.fruvio.GestionConge.historique_mouvement.controller;

import java.sql.Date;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;
import com.fruvio.GestionConge.historique_mouvement.repository.Historique_mouvementRepository;
import com.fruvio.GestionConge.historique_mouvement.service.Historique_mouvementService;

@RestController
@RequestMapping("/historiques")
@CrossOrigin(origins = "http://localhost:3000")
public class Historique_mouvementController {
    private final Historique_mouvementRepository historique_mouvementRepository;
    private final Historique_mouvementService historiqueService;

    public Historique_mouvementController(Historique_mouvementRepository historique_mouvementRepository,
                                          Historique_mouvementService historiqueService) {
        this.historique_mouvementRepository = historique_mouvementRepository;
        this.historiqueService = historiqueService;
    }

    @GetMapping({"", "/all"})
    public ResponseEntity<List<Historique_mouvement>> getAll() {
        return ResponseEntity.ok(historiqueService.getAllMouvements());
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Historique_mouvement>> getParUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(historiqueService.getMouvementsParUtilisateur(utilisateurId));
    }

    @GetMapping("/creer")
    public String get(@RequestParam Long utilisateurId, @RequestParam String typeMouvement,
            @RequestParam double quantite, @RequestParam Long type_conge_id, @RequestParam Long demande_id,
            @RequestParam String commentaire, @RequestParam Long effectue_par, @RequestParam Date date) {
        Historique_mouvement Historique = Historique_mouvement.builder().utilisateurId(utilisateurId)
                .typeMouvement(typeMouvement).quantite(quantite).type_conge_id(type_conge_id).demande_id(demande_id)
                .commentaire(commentaire).effectue_par(effectue_par).date(date).build();

        historique_mouvementRepository.save(Historique);
        return "historique";
    }
}

