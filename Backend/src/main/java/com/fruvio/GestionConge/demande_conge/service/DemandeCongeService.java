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
    public DemandeConge creerDemande(Long utilisateurId, DemandeConge demande) {
        demande.setUtilisateurId(utilisateurId);
        demande.setStatut("EN_ATTENTE");
        demande.setDateCreation(LocalDate.now());
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

    // Traiter une demande (manager)
    @Transactional
    public DemandeConge traiterDemande(Long demandeId, Long managerId, String statut, String motifRefus) {
        DemandeConge demande = demandeRepo.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande non trouvée"));
        demande.setValideePar(managerId);
        demande.setStatut(statut);
        demande.setDateValidation(LocalDate.now());
        if ("REFUSEE".equals(statut)) {
            demande.setMotifRefus(motifRefus);
        }
        DemandeConge saved = demandeRepo.save(demande);

        // Si la demande est validée, déduire du solde et enregistrer dans l'historique
        if ("VALIDEE".equals(statut) && demande.getDateDebut() != null && demande.getDateFin() != null) {
            long jours = ChronoUnit.DAYS.between(demande.getDateDebut(), demande.getDateFin()) + 1;
            if (jours <= 0) jours = 1;

            int annee = demande.getDateDebut().getYear();
            if (demande.getTypeCongeId() != null && demande.getUtilisateurId() != null) {
                Optional<Solde_conge> soldeOpt = soldeRepo.findByUtilisateurIdAndTypeCongeIdAndPeriode(
                    demande.getUtilisateurId(), demande.getTypeCongeId(), annee
                );
                if (soldeOpt.isPresent()) {
                    Solde_conge solde = soldeOpt.get();
                    solde.setSoldePris(solde.getSoldePris() + jours);
                    solde.setSoldeRestant(Math.max(0, solde.getSoldeRestant() - jours));
                    solde.setDate_maj(new Timestamp(System.currentTimeMillis()));
                    soldeRepo.save(solde);
                }

                try {
                    historiqueService.enregistrerMouvement(
                        demande.getUtilisateurId(),
                        "DEBIT_CONGE",
                        (double) jours,
                        demande.getTypeCongeId(),
                        demande.getId(),
                        "Congé validé par manager #" + managerId,
                        managerId
                    );
                } catch (Exception ignored) {}
            }

            try {
                notificationService.creerNotification(
                    demande.getUtilisateurId(),
                    "Votre demande de congé du " + demande.getDateDebut() + " au " + demande.getDateFin() + " a été VALIDÉE.",
                    "VALIDATION",
                    "/dashboard/demande"
                );
            } catch (Exception ignored) {}
        } else if ("REFUSEE".equals(statut)) {
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

        return saved;
    }

    // Annuler une demande (utilisateur)
    public DemandeConge annulerDemande(Long demandeId, Long utilisateurId) {
        DemandeConge demande = demandeRepo.findById(demandeId).orElseThrow();
        if (demande.getUtilisateurId().equals(utilisateurId) && "EN_ATTENTE".equals(demande.getStatut())) {
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
        // Renvoie toutes les demandes en attente pour validation
        return demandeRepo.findByStatut("EN_ATTENTE");
    }
}

