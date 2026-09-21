package com.fruvio.GestionConge.solde_conge.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fruvio.GestionConge.solde_conge.entity.Solde_conge;
import com.fruvio.GestionConge.solde_conge.service.Solde_congeService;

@RestController
@RequestMapping("/solde")
public class Solde_congeController {

    private final Solde_congeService soldeCongeService;

    public Solde_congeController(Solde_congeService soldeCongeService) {
        this.soldeCongeService = soldeCongeService;
    }

    // Récupérer tous les soldes (Admin et Manager pour vue d'ensemble)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping({"", "/all"})
    public ResponseEntity<List<Solde_conge>> getAllSoldes() {
        return ResponseEntity.ok(soldeCongeService.getAllSoldes());
    }

    // Récupérer tous les soldes d'un utilisateur (Consultable par l'employé, manager ou admin)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Solde_conge>> getSoldesParUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(soldeCongeService.getSoldesParUtilisateur(utilisateurId));
    }

    // Initialiser le solde d'un utilisateur pour une année (Strictement réservé à l'Admin)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/initialiser/{utilisateurId}")
    public ResponseEntity<Void> initialiserSoldesAnnuels(@PathVariable Long utilisateurId, @RequestParam int annee) {
        soldeCongeService.initialiserSoldesPourUtilisateur(utilisateurId, annee);
        return ResponseEntity.ok().build();
    }

    // Ajuster manuellement le solde (Strictement réservé à l'Admin - le manager ne contrôle pas le solde)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/ajuster/{soldeId}")
    public ResponseEntity<Solde_conge> ajusterSolde(@PathVariable Long soldeId,
            @RequestParam int nouveauNombreJoursRestants) {
        Solde_conge soldeAjuste = soldeCongeService.ajusterSolde(soldeId, nouveauNombreJoursRestants);
        return ResponseEntity.ok(soldeAjuste);
    }
}