package com.fruvio.GestionConge.utilisateur.controller;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.service.UtilisateurService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/utilisateur")
@CrossOrigin(origins="http://localhost:3000")
public class UtilisateurController {
    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/create")
    public ResponseEntity<Utilisateur> create(@RequestBody Utilisateur utilisateur) {
        Utilisateur saved = utilisateurService.createUtilisateur(utilisateur);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String motDePass = credentials.get("mot_de_pass");

    Utilisateur utilisateur = utilisateurService.login(email, motDePass);

    if (utilisateur != null) {
        return ResponseEntity.ok(utilisateur);
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Email ou mot de passe incorrect");
    }
}
    
    
    @GetMapping("/utilisateurcontrol")
    public String get(
        @RequestParam String nom,
        @RequestParam String prenom,
        @RequestParam String email,
        @RequestParam String mot_de_pass,
        @RequestParam String role,
        @RequestParam Long service_id,
        @RequestParam Long manager_id,
        @RequestParam String date_embauche,
        @RequestParam boolean actif,
        @RequestParam String date_creation
    ) {
        String test=utilisateurService.creationUtilisateur(nom, prenom, email, mot_de_pass, role, service_id, manager_id, date_embauche, actif, date_creation);
        return test;
    }
}
