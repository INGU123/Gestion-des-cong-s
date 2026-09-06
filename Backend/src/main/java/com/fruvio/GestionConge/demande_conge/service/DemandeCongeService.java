package com.fruvio.GestionConge.demande_conge.service;

import com.fruvio.GestionConge.demande_conge.entity.DemandeConge;
import com.fruvio.GestionConge.demande_conge.repository.DemandeCongeRepository;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class DemandeCongeService {

    private final DemandeCongeRepository demandeRepo;

    public DemandeCongeService(DemandeCongeRepository demandeRepo) {
        this.demandeRepo = demandeRepo;
    }

    // Créer une demande
    public DemandeConge creerDemande(Long utilisateurId, DemandeConge demande) {
        demande.setUtilisateurId(utilisateurId);
        demande.setStatut("EN_ATTENTE");
        demande.setDateCreation(new Date(System.currentTimeMillis()));
        return demandeRepo.save(demande);
    }

    // Traiter une demande (manager)
    public DemandeConge traiterDemande(Long demandeId, Long managerId, String statut, String motifRefus) {
        DemandeConge demande = demandeRepo.findById(demandeId).orElseThrow();
        demande.setValideePar(managerId);
        demande.setStatut(statut);
        demande.setDateValidation(new Date(System.currentTimeMillis()));
        if ("REFUSEE".equals(statut)) {
            demande.setMotifRefus(motifRefus);
        }
        return demandeRepo.save(demande);
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

    // Récupérer les demandes en attente pour un manager
    public List<DemandeConge> getDemandesEnAttentePourManager(Long managerId) {
        return demandeRepo.findByValideeParAndStatut(managerId, "EN_ATTENTE");
    }
}
