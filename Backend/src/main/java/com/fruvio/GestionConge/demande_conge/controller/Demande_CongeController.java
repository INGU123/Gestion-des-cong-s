package com.fruvio.GestionConge.demande_conge.controller;

import com.fruvio.GestionConge.demande_conge.entity.DemandeConge;
import com.fruvio.GestionConge.demande_conge.service.DemandeCongeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/conge")
public class Demande_CongeController {

    private final DemandeCongeService congeService;

    public Demande_CongeController(DemandeCongeService congeService) {
        this.congeService = congeService;
    }

    // Soumettre une demande de congé (Tous les collaborateurs)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @PostMapping("/demander/{utilisateurId}")
    public ResponseEntity<DemandeConge> demanderConge(@PathVariable Long utilisateurId,
            @RequestBody DemandeConge demande) {
        DemandeConge nouvelleDemande = congeService.creerDemande(utilisateurId, demande);
        return new ResponseEntity<>(nouvelleDemande, HttpStatus.CREATED);
    }

    // Valider ou refuser une demande (Réservé au Manager et Admin)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@PutMapping("/{demandeId}/traiter")
public ResponseEntity<DemandeConge> traiterDemande(
    @PathVariable Long demandeId,
    @RequestParam Long managerId,
    @RequestParam String statut,
    @RequestParam(required = false) String motifRefus) {
    DemandeConge demandeTraitee = congeService.traiterDemande(demandeId, managerId, statut, motifRefus);
    return ResponseEntity.ok(demandeTraitee);
}
    // Annuler une demande par le collaborateur
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @PutMapping("/{demandeId}/annuler")
    public ResponseEntity<DemandeConge> annulerDemande(@PathVariable Long demandeId, @RequestParam Long utilisateurId) {
        DemandeConge demandeAnnulee = congeService.annulerDemande(demandeId, utilisateurId);
        return ResponseEntity.ok(demandeAnnulee);
    }

    // Liste des demandes d'un collaborateur
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/mes-demandes/{utilisateurId}")
    public ResponseEntity<List<DemandeConge>> getMesDemandes(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(congeService.getDemandesParUtilisateur(utilisateurId));
    }

    // Liste des demandes en attente pour un manager
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/manager/{managerId}/en-attente")
    public ResponseEntity<List<DemandeConge>> getDemandesEnAttenteManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(congeService.getDemandesEnAttentePourManager(managerId));
    }

    // Liste de toutes les demandes en attente (Manager / Admin)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/en-attente")
    public ResponseEntity<List<DemandeConge>> getToutesDemandesEnAttente() {
        return ResponseEntity.ok(congeService.getToutesLesDemandesEnAttente());
    }

    // Liste de toutes les demandes (Manager / Admin)
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/all")
    public ResponseEntity<List<DemandeConge>> getAllDemandes() {
        return ResponseEntity.ok(congeService.getToutesLesDemandes());
    }
}