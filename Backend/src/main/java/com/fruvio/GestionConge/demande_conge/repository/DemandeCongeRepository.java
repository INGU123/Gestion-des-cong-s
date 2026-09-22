package com.fruvio.GestionConge.demande_conge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fruvio.GestionConge.demande_conge.entity.DemandeConge;

public interface DemandeCongeRepository extends JpaRepository<DemandeConge, Long> {
    List<DemandeConge> findByUtilisateurId(Long utilisateurId);

    List<DemandeConge> findByValideeParAndStatut(Long managerId, String statut);

    List<DemandeConge> findByStatut(String statut);
}