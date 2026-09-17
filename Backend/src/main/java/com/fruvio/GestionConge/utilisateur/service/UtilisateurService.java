package com.fruvio.GestionConge.utilisateur.service;

import java.util.Optional;
import java.util.List;

import org.springframework.stereotype.Service;

import com.fruvio.GestionConge.utilisateur.entity.Role;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // Création simple
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Role.EMPLOYE); // rôle par défaut
        }
        return utilisateurRepository.save(utilisateur);
    }

    // Login basique
    public Utilisateur login(String email, String motDePass) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            if (user.getMot_de_pass().equals(motDePass)) {
                return user;
            }
        }
        return null;
    }

    // Création avec paramètres bruts
    public String creationUtilisateur(
        String nom,
        String prenom,
        String email,
        String mot_de_pass,
        String roleStr,
        Long service_id,
        Long manager_id,
        String date_embauche,
        boolean actif,
        String date_creation
    ) {
        Utilisateur utilisateurExistant = utilisateurRepository.findByNomAndPrenom(nom, prenom);
        if (utilisateurExistant == null) {
            Role role = Role.valueOf(roleStr.toUpperCase()); // conversion String → Enum

            Utilisateur pers1 = Utilisateur.builder()
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .mot_de_pass(mot_de_pass)
                .role(role)
                .service_id(service_id)
                .manager_id(manager_id)
                .date_embauche(date_embauche)
                .actif(actif)
                .date_creation(date_creation)
                .build();

            utilisateurRepository.save(pers1);
            return "Utilisateur créé";
        } else {
            return "Utilisateur déjà répertorié...";
        }
    }

    // Récupération
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    // Mise à jour
    public Utilisateur updateUtilisateur(Utilisateur details) {
        Utilisateur user = null;
        if (details.getId() != null) {
            user = utilisateurRepository.findById(details.getId()).orElse(null);
        }
        if (user == null && details.getEmail() != null) {
            user = utilisateurRepository.findByEmail(details.getEmail()).orElse(null);
        }
        if (user == null) {
            return null;
        }

        if (details.getNom() != null && !details.getNom().isEmpty()) {
            user.setNom(details.getNom());
        }
        if (details.getPrenom() != null && !details.getPrenom().isEmpty()) {
            user.setPrenom(details.getPrenom());
        }
        if (details.getEmail() != null && !details.getEmail().isEmpty()) {
            user.setEmail(details.getEmail());
        }
        if (details.getMot_de_pass() != null && !details.getMot_de_pass().isEmpty()) {
            user.setMot_de_pass(details.getMot_de_pass());
        }
        if (details.getRole() != null) {
            user.setRole(details.getRole()); // plus besoin de .isEmpty()
        }
        if (details.getService_id() != null) {
            user.setService_id(details.getService_id());
        }
        if (details.getManager_id() != null) {
            user.setManager_id(details.getManager_id());
        }

        return utilisateurRepository.save(user);
    }
}
