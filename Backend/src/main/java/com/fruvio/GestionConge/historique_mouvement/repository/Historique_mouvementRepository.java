package com.fruvio.GestionConge.historique_mouvement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fruvio.GestionConge.historique_mouvement.entity.Historique_mouvement;

@Repository
public interface Historique_mouvementRepository extends JpaRepository<Historique_mouvement, Long> {
    List<Historique_mouvement> findByUtilisateurId(Long utilisateurId);

    List<Historique_mouvement> findByUtilisateurIdIn(List<Long> utilisateurIds);
}
