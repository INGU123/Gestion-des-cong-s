
package com.fruvio.GestionConge.solde_conge.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.fruvio.GestionConge.solde_conge.entity.Solde_conge;
import com.fruvio.GestionConge.solde_conge.service.Solde_congeService;

@RestController
@RequestMapping("/solde")
@CrossOrigin(origins = "http://localhost:3000")
public class Solde_congeController {

    private final Solde_congeService Solde_congeService;

    public Solde_congeController(Solde_congeService Solde_congeService) {
        this.Solde_congeService = Solde_congeService;
    }

    // Récupérer tous les soldes d'un utilisateur (Congés Payés, Maladie, etc.)
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Solde_conge>> getSoldesParUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(Solde_congeService.getSoldesParUtilisateur(utilisateurId));
    }

    // Initialiser le solde d'un nouvel utilisateur pour une année
    @PostMapping("/initialiser/{utilisateurId}")
    public ResponseEntity<Void> initialiserSoldesAnnuels(@PathVariable Long utilisateurId, @RequestParam int annee) {
        Solde_congeService.initialiserSoldesPourUtilisateur(utilisateurId, annee);
        return ResponseEntity.ok().build();
    }

    // Ajuster manuellement le solde d'un utilisateur (par un RH / Admin)
    @PutMapping("/ajuster/{soldeId}")
    public ResponseEntity<Solde_conge> ajusterSolde(@PathVariable Long soldeId,
            @RequestParam int nouveauNombreJoursRestants) {
        Solde_conge soldeAjuste = Solde_congeService.ajusterSolde(soldeId, nouveauNombreJoursRestants);
        return ResponseEntity.ok(soldeAjuste);
    }
}