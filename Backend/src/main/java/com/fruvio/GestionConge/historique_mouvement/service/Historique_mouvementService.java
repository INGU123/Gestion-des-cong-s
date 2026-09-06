package com.fruvio.GestionConge.historique_mouvement.service;

import java.sql.Date;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;
import com.fruvio.GestionConge.historique_mouvement.repository.Historique_mouvementRepository;

@Service
public class Historique_mouvementService {

    private final Historique_mouvementRepository historique_mouvementRepository;

    public Historique_mouvementService(Historique_mouvementRepository historique_mouvementRepository) {
        this.historique_mouvementRepository = historique_mouvementRepository;
    }

    @Transactional
    public void enregistrerMouvement(Long utilisateurId, String typeMouvement, double quantite, Long type_conge_id,
            Long demande_id, String commentaire, Long effectue_par) {
        Historique_mouvement mouvement = new Historique_mouvement();

        // ⚠️ Correction : j’utilise les setters EXACTS de ton entity (avec underscores)
        mouvement.setUtilisateurId(utilisateurId);
        mouvement.setTypeMouvement(typeMouvement);
        mouvement.setQuantite(quantite);
        mouvement.setType_conge_id(type_conge_id);
        mouvement.setDemande_id(demande_id);
        mouvement.setCommentaire(commentaire);
        mouvement.setEffectue_par(effectue_par);
        mouvement.setDate(new Date(System.currentTimeMillis())); // date actuelle

        historique_mouvementRepository.save(mouvement);
    }

    public List<Historique_mouvement> getAllMouvements() {
        return historique_mouvementRepository.findAll();
    }

    public List<Historique_mouvement> getMouvementsParUtilisateur(Long utilisateurId) {
        return historique_mouvementRepository.findByUtilisateurId(utilisateurId);
    }
}
