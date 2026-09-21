package com.fruvio.GestionConge.demande_conge.service;

import com.fruvio.GestionConge.demande_conge.entity.DemandeConge;
import com.fruvio.GestionConge.demande_conge.repository.DemandeCongeRepository;
import com.fruvio.GestionConge.historique_mouvement.service.Historique_mouvementService;
import com.fruvio.GestionConge.notifications_conge.service.Notification_congeService;
import com.fruvio.GestionConge.solde_conge.entity.Solde_conge;
import com.fruvio.GestionConge.solde_conge.repository.Solde_congeRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemandeCongeService {

    private final DemandeCongeRepository demandeRepo;
    private final Solde_congeRepository soldeRepo;
    private final Historique_mouvementService historiqueService;
    private final Notification_congeService notificationService;

    public DemandeCongeService(DemandeCongeRepository demandeRepo,
                               Solde_congeRepository soldeRepo,
                               Historique_mouvementService historiqueService,
                               Notification_congeService notificationService) {
        this.demandeRepo = demandeRepo;
        this.soldeRepo = soldeRepo;
        this.historiqueService = historiqueService;
        this.notificationService = notificationService;
    }

    // Créer une demande
    @Transactional
    public DemandeConge creerDemande(Long utilisateurId, DemandeConge demande) {
        demande.setUtilisateurId(utilisateurId);
        demande.setStatut("EN_ATTENTE");
        demande.setDateCreation(LocalDate.now());

        if (demande.getDateDebut() != null && demande.getDateFin() != null) {
            long diff = ChronoUnit.DAYS.between(demande.getDateDebut(), demande.getDateFin()) + 1;
            demande.setNombreJours((int) Math.max(1, diff));
        } else {
            demande.setNombreJours(1);
        }

        DemandeConge saved = demandeRepo.save(demande);

        try {
            notificationService.creerNotification(
                utilisateurId,
                "Votre demande de congé du " + demande.getDateDebut() + " au " + demande.getDateFin() + " a été soumise.",
                "INFO",
                "/dashboard/demande"
            );
        } catch (Exception ignored) {}

        return saved;
    }

    // Traiter une demande (manager / admin)
    @Transactional
    public DemandeConge traiterDemande(Long demandeId, Long managerId, String statut, String motifRefus) {
        DemandeConge demande = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée avec l'ID : " + demandeId));

        demande.setValideePar(managerId);
        demande.setStatut(statut);
        demande.setDateValidation(LocalDate.now());

        if ("REFUSEE".equalsIgnoreCase(statut)) {
            demande.setMotifRefus(motifRefus);
        }

        // Vérification flexible du statut ("VALIDE", "VALIDEE", "APPROVED", etc.)
        boolean estValidee = "VALIDEE".equalsIgnoreCase(statut) || "VALIDE".equalsIgnoreCase(statut) || "APPROVED".equalsIgnoreCase(statut);

        // Si la demande est validée, déduire du solde
        if (estValidee && demande.getDateDebut() != null && demande.getDateFin() != null) {
            long joursLong = ChronoUnit.DAYS.between(demande.getDateDebut(), demande.getDateFin()) + 1;
            int jours = (int) Math.max(1, joursLong);
            demande.setNombreJours(jours);

            int annee = demande.getDateDebut().getYear();

            if (demande.getTypeCongeId() != null && demande.getUtilisateurId() != null) {
                
                // Recherche du solde correspondant
                Optional<Solde_conge> soldeOpt = soldeRepo.findByUtilisateurIdAndTypeCongeIdAndPeriode(
                    demande.getUtilisateurId(), demande.getTypeCongeId(), annee
                );

                if (soldeOpt.isPresent()) {
                    Solde_conge solde = soldeOpt.get();
                    int prisActuel = (solde.getSoldePris() != null) ? solde.getSoldePris() : 0;
                    int acquisActuel = (solde.getSoldeAquis() != null) ? solde.getSoldeAquis() : 0;

                    int nouveauSoldePris = prisActuel + jours;
                    solde.setSoldePris(nouveauSoldePris);
                    solde.setSoldeRestant(Math.max(0, acquisActuel - nouveauSoldePris));
                    solde.setDate_maj(new Timestamp(System.currentTimeMillis()));

                    // Déclenche explicitement le UPDATE SQL sur la table solde_conge
                    soldeRepo.save(solde);
                } else {
                    throw new IllegalStateException("Erreur : Aucun solde trouvé pour l'utilisateur #" 
                        + demande.getUtilisateurId() + ", le type de congé #" + demande.getTypeCongeId() 
                        + " et la période " + annee);
                }

                // Enregistrement de l'historique
                try {
                    historiqueService.enregistrerMouvement(
                        demande.getUtilisateurId(),
                        "DEBIT_CONGE",
                        jours,
                        demande.getTypeCongeId(),
                        demande.getId(),
                        "Congé validé par validateur #" + managerId,
                        managerId
                    );
                } catch (Exception ignored) {}
            }

            // Notification de validation
            try {
                notificationService.creerNotification(
                    demande.getUtilisateurId(),
                    "Votre demande de congé du " + demande.getDateDebut() + " au " + demande.getDateFin() + " a été VALIDÉE.",
                    "VALIDATION",
                    "/dashboard/demande"
                );
            } catch (Exception ignored) {}

        } else if ("REFUSEE".equalsIgnoreCase(statut)) {
            // Notification de refus
            try {
                notificationService.creerNotification(
                    demande.getUtilisateurId(),
                    "Votre demande de congé du " + demande.getDateDebut() + " a été REFUSÉE" + 
                    (motifRefus != null && !motifRefus.isEmpty() ? " (Motif : " + motifRefus + ")" : "."),
                    "REFUS",
                    "/dashboard/demande"
                );
            } catch (Exception ignored) {}
        }

        return demandeRepo.save(demande);
    }

    // Annuler une demande (utilisateur)
    @Transactional
    public DemandeConge annulerDemande(Long demandeId, Long utilisateurId) {
        DemandeConge demande = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée avec l'ID : " + demandeId));

        if (demande.getUtilisateurId().equals(utilisateurId) && "EN_ATTENTE".equalsIgnoreCase(demande.getStatut())) {
            demande.setStatut("ANNULEE");
        }
        return demandeRepo.save(demande);
    }

    // Récupérer les demandes d’un utilisateur
    public List<DemandeConge> getDemandesParUtilisateur(Long utilisateurId) {
        return demandeRepo.findByUtilisateurId(utilisateurId);
    }

    // Récupérer toutes les demandes en attente
    public List<DemandeConge> getToutesLesDemandesEnAttente() {
        return demandeRepo.findByStatut("EN_ATTENTE");
    }

    // Récupérer toutes les demandes
    public List<DemandeConge> getToutesLesDemandes() {
        return demandeRepo.findAll();
    }

    // Récupérer les demandes en attente pour un manager
    public List<DemandeConge> getDemandesEnAttentePourManager(Long managerId) {
        return demandeRepo.findByStatut("EN_ATTENTE");
    }
}