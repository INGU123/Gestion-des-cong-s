package com.fruvio.GestionConge.utilisateur.controller;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.fruvio.GestionConge.utilisateur.config.JwtUtil;
import com.fruvio.GestionConge.utilisateur.entity.Role;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.service.EmailService;
import com.fruvio.GestionConge.utilisateur.service.UtilisateurService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/utilisateur")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public UtilisateurController(UtilisateurService utilisateurService, EmailService emailService, JwtUtil jwtUtil) {
        this.utilisateurService = utilisateurService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String matricule = credentials.getOrDefault("matricule", credentials.get("email"));
        String motDePass = credentials.getOrDefault("password",
                credentials.getOrDefault("mot_de_pass", credentials.get("motDePass")));

        try {
            Utilisateur utilisateur = utilisateurService.login(matricule, motDePass);

            if (utilisateur != null) {
                String roleName = (utilisateur.getRole() != null) ? utilisateur.getRole().name() : "EMPLOYE";
                String token = jwtUtil.generateToken(utilisateur.getMatricule(), roleName);
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "utilisateur", utilisateur
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Matricule ou mot de passe incorrect");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Matricule ou mot de passe incorrect");
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody(required = false) Map<String, String> body,
            @RequestParam(value = "email", required = false) String paramEmail) {

        String email = null;

        if (body != null && body.containsKey("email")) {
            email = body.get("email");
        } else if (paramEmail != null) {
            email = paramEmail;
        }

        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("L'adresse e-mail est obligatoire.");
        }

        try {
            Utilisateur utilisateur = utilisateurService.findByEmail(email.trim());

            if (utilisateur == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Aucun compte n'est associé à cet e-mail.");
            }

            String token = UUID.randomUUID().toString();
            utilisateurService.saveResetToken(utilisateur, token);

            emailService.sendResetPasswordEmail(utilisateur.getEmail(), token);

            return ResponseEntity.ok("Lien de réinitialisation envoyé avec succès.");

        } catch (MailException e) {
            System.err.println("Erreur d'envoi SMTP : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de l'envoi de l'e-mail. Vérifiez la configuration SMTP du serveur.");
        } catch (Exception e) {
            System.err.println("Erreur interne lors du forgot-password : " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Une erreur interne est survenue sur le serveur.");
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> create(@Valid @RequestBody Utilisateur utilisateur) {
        Utilisateur saved = utilisateurService.createUtilisateur(utilisateur);
        
        // Retourne à la fois l'utilisateur créé et le mot de passe généré en clair
        return new ResponseEntity<>(Map.of(
            "utilisateur", saved,
            "generatedPassword", saved.getPassword(),
            "message", "Utilisateur créé avec succès. Un e-mail contenant les identifiants lui a été envoyé."
        ), HttpStatus.CREATED);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @PutMapping("/update")
    public ResponseEntity<?> update(@RequestBody Map<String, Object> body) {
        Long id = body.get("id") != null ? Long.valueOf(body.get("id").toString()) : null;

        if (id == null) {
            return ResponseEntity.badRequest().body("L'ID de l'utilisateur est obligatoire pour la mise à jour.");
        }

        Utilisateur u = utilisateurService.getUtilisateurById(id);
        if (u == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Utilisateur non trouvé");
        }

        if (body.get("matricule") != null) u.setMatricule((String) body.get("matricule"));
        if (body.get("nom") != null) u.setNom((String) body.get("nom"));
        if (body.get("prenom") != null) u.setPrenom((String) body.get("prenom"));
        if (body.get("email") != null) u.setEmail((String) body.get("email"));

        String motDePass = (String) (body.get("password") != null ? body.get("password") : body.get("mot_de_pass"));
        if (motDePass != null && !motDePass.trim().isEmpty()) {
            u.setPassword(motDePass);
        }

        String roleStr = (String) body.get("role");
        if (roleStr != null && !roleStr.isEmpty()) {
            try {
                u.setRole(Role.valueOf(roleStr.toUpperCase()));
            } catch (Exception ignored) {}
        }

        Utilisateur updated = utilisateurService.updateUtilisateur(u);
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/all")
    public ResponseEntity<java.util.List<Utilisateur>> getAll() {
        return ResponseEntity.ok(utilisateurService.getAllUtilisateurs());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYE')")
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getById(@PathVariable Long id) {
        Utilisateur u = utilisateurService.getUtilisateurById(id);
        if (u != null) {
            return ResponseEntity.ok(u);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/utilisateurcontrol")
    public String get(
        @RequestParam String matricule,
        @RequestParam String nom,
        @RequestParam String prenom,
        @RequestParam String email,
        @RequestParam(required = false) String password,
        @RequestParam String role,
        @RequestParam Long service_id,
        @RequestParam Long manager_id,
        @RequestParam String date_embauche,
        @RequestParam boolean actif,
        @RequestParam String date_creation
    ) {
        LocalDate embauche = null;
        LocalDate creation = null;

        try { embauche = LocalDate.parse(date_embauche); } catch (Exception ignored) {}
        try { creation = LocalDate.parse(date_creation); } catch (Exception ignored) {}

        return utilisateurService.creationUtilisateur(
            matricule, nom, prenom, email, password, role,
            service_id, manager_id, embauche, actif, creation
        );
    }
}