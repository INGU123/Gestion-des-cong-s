package com.fruvio.GestionConge.historique_mouvement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;

public interface Historique_mouvementRepository extends JpaRepository <Historique_mouvement,Long> {
     List<Historique_mouvement> findByUtilisateurId(Long utilisateurId);
}
