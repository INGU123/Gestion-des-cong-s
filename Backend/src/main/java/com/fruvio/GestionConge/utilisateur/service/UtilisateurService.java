package com.fruvio.GestionConge.utilisateur.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.repository.UtilisateurRepository;


@Service
public class UtilisateurService {
    private final UtilisateurRepository utilisateurRepository;
    public UtilisateurService(UtilisateurRepository utilisateurRepository){
        this.utilisateurRepository=utilisateurRepository;
    }

    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }
    public Utilisateur save(Utilisateur utilisateur){
        return utilisateurRepository.save(utilisateur);
    }

    public Utilisateur login(String email, String motDePass) {
    Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmail(email);
    if (utilisateurOpt.isPresent()) {
        Utilisateur user = utilisateurOpt.get();
        // Vérification du mot de passe (À adapter si vous utilisez BCrypt / de l'encodage)
        if (user.getMot_de_pass().equals(motDePass)) {
            return user;
        }
    }
    return null; // Identifiants incorrects
}
    

    public String creationUtilisateur(
         String nom,
         String prenom,
         String email,
         String mot_de_pass,
         String role,
         Long service_id,
         Long manager_id,
         String date_embauche,
         boolean actif,
         String date_creation
    ){
        Utilisateur utilisateurExistant=utilisateurRepository.findByNomAndPrenom(nom,prenom);
        if(utilisateurExistant==null){
            Utilisateur pers1=Utilisateur
            .builder()
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
            return "Utilisateur creer";  
        }else{
            return "Utilisateur deja repertorier...";
        }
    }
}
