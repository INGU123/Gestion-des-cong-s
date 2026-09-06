package com.fruvio.GestionConge.historique_mouvement.controller;

import java.sql.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;
import com.fruvio.GestionConge.historique_mouvement.repository.Historique_mouvementRepository;

@RestController
public class Historique_mouvementController {
    private final Historique_mouvementRepository historique_mouvementRepository;

    public Historique_mouvementController(Historique_mouvementRepository historique_mouvementRepository) {
        this.historique_mouvementRepository = historique_mouvementRepository;
    }

    @GetMapping("/historique")
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
