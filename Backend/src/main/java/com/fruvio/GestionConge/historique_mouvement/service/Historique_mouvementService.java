package com.fruvio.GestionConge.historique_mouvement.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;
import com.fruvio.GestionConge.historique_mouvement.repository.Historique_mouvementRepository;
import com.fruvio.GestionConge.utilisateur.entity.Role;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.repository.UtilisateurRepository;

@Service
public class Historique_mouvementService {

    private final Historique_mouvementRepository historique_mouvementRepository;
    private final UtilisateurRepository utilisateurRepository;

    public Historique_mouvementService(Historique_mouvementRepository historique_mouvementRepository,
                                       UtilisateurRepository utilisateurRepository) {
        this.historique_mouvementRepository = historique_mouvementRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Transactional
    public void enregistrerMouvement(Long utilisateurId, String typeMouvement, Integer quantite, Long type_conge_id,
            Long demande_id, String commentaire, Long effectue_par) {
        Historique_mouvement mouvement = new Historique_mouvement();

        mouvement.setUtilisateurId(utilisateurId);
        mouvement.setTypeMouvement(typeMouvement);
        mouvement.setQuantite(quantite != null ? quantite : 0);
        mouvement.setType_conge_id(type_conge_id);
        mouvement.setDemande_id(demande_id);
        mouvement.setCommentaire(commentaire);
        mouvement.setEffectue_par(effectue_par);
        mouvement.setDate(new Date(System.currentTimeMillis()));

        historique_mouvementRepository.save(mouvement);
    }

    public List<Historique_mouvement> getAllMouvements(Utilisateur currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Authentification requise.");
        }

        if (currentUser.getRole() == Role.ADMIN) {
            return historique_mouvementRepository.findAll();
        }

        if (currentUser.getRole() == Role.MANAGER) {
            List<Utilisateur> subordinates = utilisateurRepository.findByManagerId(currentUser.getId());
            List<Long> ids = new ArrayList<>(subordinates.stream().map(Utilisateur::getId).toList());
            if (!ids.contains(currentUser.getId())) {
                ids.add(currentUser.getId());
            }
            if (ids.isEmpty()) {
                return Collections.emptyList();
            }
            return historique_mouvementRepository.findByUtilisateurIdIn(ids);
        }

        throw new AccessDeniedException("Accès refusé.");
    }

    public List<Historique_mouvement> getMouvementsParUtilisateur(Long utilisateurId, Utilisateur currentUser) {
        if (currentUser == null) {
            throw new AccessDeniedException("Authentification requise.");
        }

        if (currentUser.getRole() == Role.EMPLOYE && !Objects.equals(currentUser.getId(), utilisateurId)) {
            throw new AccessDeniedException("Vous ne pouvez consulter que votre propre historique.");
        }

        if (currentUser.getRole() == Role.MANAGER) {
            if (!Objects.equals(currentUser.getId(), utilisateurId)) {
                Utilisateur target = utilisateurRepository.findById(utilisateurId).orElse(null);
                if (target == null || !Objects.equals(target.getManager_id(), currentUser.getId())) {
                    throw new AccessDeniedException("Accès non autorisé : cet employé n'appartient pas à votre équipe.");
                }
            }
        }

        return historique_mouvementRepository.findByUtilisateurId(utilisateurId);
    }
}
