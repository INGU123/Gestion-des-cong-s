package com.fruvio.GestionConge.utilisateur.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {
    Optional<Utilisateur> findByMatricule(String matricule);
    Optional<Utilisateur> findByNomAndPrenom(String nom, String prenom);
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findByResetToken(String resetToken);
}