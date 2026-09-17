package com.fruvio.GestionConge.demande_conge.controller;

import com.fruvio.GestionConge.demande_conge.entity.DemandeConge;
import com.fruvio.GestionConge.demande_conge.service.DemandeCongeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/conge")
@CrossOrigin(origins = "http://localhost:3000")
public class Demande_CongeController {

    private final DemandeCongeService congeService;

    public Demande_CongeController(DemandeCongeService congeService) {
        this.congeService = congeService;
    }

    // Soumettre une demande de congé
    @PostMapping("/demander/{utilisateurId}")
    public ResponseEntity<DemandeConge> demanderConge(@PathVariable Long utilisateurId,
            @RequestBody DemandeConge demande) {
        DemandeConge nouvelleDemande = congeService.creerDemande(utilisateurId, demande);
        return new ResponseEntity<>(nouvelleDemande, HttpStatus.CREATED);
    }

    // Valider ou refuser une demande (pour le Manager/Admin)
    @PutMapping("/{demandeId}/traiter")
    public ResponseEntity<DemandeConge> traiterDemande(@PathVariable Long demandeId, @RequestParam Long managerId,
            @RequestParam String statut, // "VALIDEE" ou "REFUSEE"
            @RequestParam(required = false) String motifRefus) {
        DemandeConge demandeTraitee = congeService.traiterDemande(demandeId, managerId, statut, motifRefus);
        return ResponseEntity.ok(demandeTraitee);
    }

    // Annuler une demande par l'utilisateur
    @PutMapping("/{demandeId}/annuler")
    public ResponseEntity<DemandeConge> annulerDemande(@PathVariable Long demandeId, @RequestParam Long utilisateurId) {
        DemandeConge demandeAnnulee = congeService.annulerDemande(demandeId, utilisateurId);
        return ResponseEntity.ok(demandeAnnulee);
    }

    // Liste des demandes d'un utilisateur
    @GetMapping("/mes-demandes/{utilisateurId}")
    public ResponseEntity<List<DemandeConge>> getMesDemandes(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(congeService.getDemandesParUtilisateur(utilisateurId));
    }

    // Liste des demandes en attente pour un manager
    @GetMapping("/manager/{managerId}/en-attente")
    public ResponseEntity<List<DemandeConge>> getDemandesEnAttenteManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(congeService.getDemandesEnAttentePourManager(managerId));
    }

    // Liste de toutes les demandes en attente
    @GetMapping("/en-attente")
    public ResponseEntity<List<DemandeConge>> getToutesDemandesEnAttente() {
        return ResponseEntity.ok(congeService.getToutesLesDemandesEnAttente());
    }

    // Liste de toutes les demandes
    @GetMapping("/all")
    public ResponseEntity<List<DemandeConge>> getAllDemandes() {
        return ResponseEntity.ok(congeService.getToutesLesDemandes());
    }
}