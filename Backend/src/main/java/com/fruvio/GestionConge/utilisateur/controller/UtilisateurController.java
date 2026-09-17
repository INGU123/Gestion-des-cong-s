package com.fruvio.GestionConge.utilisateur.controller;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fruvio.GestionConge.utilisateur.config.JwtUtil;
import com.fruvio.GestionConge.utilisateur.entity.Role;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.service.UtilisateurService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/utilisateur")
@CrossOrigin(origins="http://localhost:3000")
public class UtilisateurController {
    
    private final UtilisateurService utilisateurService;
    private final JwtUtil jwtUtil;
    
    public UtilisateurController(UtilisateurService utilisateurService,JwtUtil jwtUtil) {
        this.utilisateurService = utilisateurService;
        this.jwtUtil=jwtUtil;
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Utilisateur> create(@Valid @RequestBody Utilisateur utilisateur) {
        Utilisateur saved = utilisateurService.createUtilisateur(utilisateur);
        return ResponseEntity.ok(saved);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        Long id = null;
        if (body.get("id") != null) {
            try {
                id = Long.valueOf(body.get("id").toString());
            } catch (Exception ignored) {}
        }
        String nom = (String) body.get("nom");
        String prenom = (String) body.get("prenom");
        String email = (String) body.get("email");
        String motDePass = (String) (body.get("mot_de_pass") != null ? body.get("mot_de_pass") : body.get("motDePass"));
        String role = (String) body.get("role");

        Utilisateur u = new Utilisateur();
        u.setId(id);
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setMot_de_pass(motDePass);
        u.setRole(Role.valueOf(role.toUpperCase()));

        Utilisateur updated = utilisateurService.updateUtilisateur(u);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<java.util.List<Utilisateur>> getAll() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getById(@org.springframework.web.bind.annotation.PathVariable Long id) {
        Utilisateur u = utilisateurService.getUtilisateurById(id);
        if (u != null) {
            return ResponseEntity.ok(u);
        }
        return ResponseEntity.notFound().build();
    }


    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
    String email = credentials.get("email");
    String motDePass = credentials.get("mot_de_pass");

    Utilisateur utilisateur = utilisateurService.login(email, motDePass);

    if (utilisateur != null) {
        String token = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole().name());
        return ResponseEntity.ok(Map.of(
            "token", token,
            "utilisateur", utilisateur
        ));
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
