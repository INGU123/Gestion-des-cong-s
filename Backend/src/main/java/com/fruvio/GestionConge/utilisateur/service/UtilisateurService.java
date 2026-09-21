package com.fruvio.GestionConge.utilisateur.service;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.Optional;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fruvio.GestionConge.solde_conge.service.Solde_congeService;
import com.fruvio.GestionConge.utilisateur.entity.Role;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.repository.UtilisateurRepository;

@Service
public class UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;
    private final Solde_congeService soldeCongeService;
    private final EmailService emailService;

    private static final String CHAR_LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String CHAR_UPPER = CHAR_LOWER.toUpperCase();
    private static final String NUMBER = "0123456789";
    private static final String PASSWORD_ALLOW_BASE = CHAR_LOWER + CHAR_UPPER + NUMBER;
    private static final SecureRandom random = new SecureRandom();

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              PasswordEncoder passwordEncoder,
                              @Lazy Solde_congeService soldeCongeService,
                              EmailService emailService) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.soldeCongeService = soldeCongeService;
        this.emailService = emailService;
    }

    // --- Générateur de mot de passe automatique ---
    public String generateRandomPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(PASSWORD_ALLOW_BASE.length());
            sb.append(PASSWORD_ALLOW_BASE.charAt(rndCharAt));
        }
        return sb.toString();
    }

    // --- Méthodes pour la réinitialisation de mot de passe ---

    public Utilisateur findByEmail(String email) {
        if (email == null) return null;
        return utilisateurRepository.findByEmail(email.trim()).orElse(null);
    }

    public void saveResetToken(Utilisateur utilisateur, String token) {
        if (utilisateur != null) {
            utilisateur.setResetToken(token);
            utilisateurRepository.save(utilisateur);
        }
    }

    public Utilisateur findByResetToken(String resetToken) {
        if (resetToken == null) return null;
        return utilisateurRepository.findByResetToken(resetToken).orElse(null);
    }

    // --- Création simple avec mot de passe automatique ---
    public Utilisateur createUtilisateur(Utilisateur utilisateur) {
        if (utilisateur.getRole() == null) {
            utilisateur.setRole(Role.EMPLOYE);
        }

        String rawPassword;
        if (utilisateur.getPassword() != null && !utilisateur.getPassword().trim().isEmpty()) {
            rawPassword = utilisateur.getPassword().trim();
        } else {
            // Génération automatique d'un mot de passe de 10 caractères
            rawPassword = generateRandomPassword(10);
        }

        utilisateur.setPassword(passwordEncoder.encode(rawPassword));

        if (utilisateur.getDate_creation() == null) {
            utilisateur.setDate_creation(LocalDate.now());
        }
        utilisateur.setActif(true);

        Utilisateur saved = utilisateurRepository.save(utilisateur);

        // Envoi de l'e-mail avec le mot de passe en clair (avant hashage)
        if (saved.getEmail() != null && !saved.getEmail().isEmpty()) {
            try {
                emailService.sendWelcomeEmail(saved.getEmail(), saved.getMatricule(), rawPassword);
            } catch (Exception e) {
                System.err.println("Avertissement : échec de l'envoi du mail de bienvenue : " + e.getMessage());
            }
        }

        try {
            int annee = LocalDate.now().getYear();
            soldeCongeService.initialiserSoldesPourUtilisateur(saved.getId(), annee);
        } catch (Exception e) {
            System.err.println("Avertissement : initialisation des soldes : " + e.getMessage());
        }

        // On remet temporairement le mot de passe clair pour le retourner dans le contrôleur si besoin
        saved.setPassword(rawPassword);
        return saved;
    }

    // --- Login avec support BCrypt et vérification par Matricule ---
    public Utilisateur login(String matricule, String motDePass) {
        if (matricule == null || motDePass == null) {
            return null;
        }
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByMatricule(matricule.trim());
        if (utilisateurOpt.isPresent()) {
            Utilisateur user = utilisateurOpt.get();
            String storedPass = user.getPassword();
            if (storedPass == null) {
                return null;
            }

            boolean matches = false;
            if (storedPass.startsWith("$2a$") || storedPass.startsWith("$2b$") || storedPass.startsWith("$2y$")) {
                matches = passwordEncoder.matches(motDePass, storedPass);
            } else {
                matches = storedPass.equals(motDePass);
                if (matches) {
                    user.setPassword(passwordEncoder.encode(motDePass));
                    utilisateurRepository.save(user);
                }
            }

            if (matches) {
                return user;
            }
        }
        return null;
    }

    // --- Création avec paramètres bruts incluant le matricule ---
    public String creationUtilisateur(
        String matricule,
        String nom,
        String prenom,
        String email,
        String password,
        String roleStr,
        Long service_id,
        Long manager_id,
        LocalDate date_embauche,
        boolean actif,
        LocalDate date_creation
    ) {
        Optional<Utilisateur> utilisateurExistant = utilisateurRepository.findByNomAndPrenom(nom, prenom);
        if (utilisateurExistant.isEmpty()) {
            Role role = Role.EMPLOYE;
            try {
                if (roleStr != null) {
                    role = Role.valueOf(roleStr.toUpperCase());
                }
            } catch (Exception ignored) {}

            String rawPassword = (password != null && !password.trim().isEmpty()) 
                    ? password.trim() 
                    : generateRandomPassword(10);

            Utilisateur pers1 = Utilisateur.builder()
                .matricule(matricule)
                .nom(nom)
                .prenom(prenom)
                .email(email)
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .service_id(service_id)
                .manager_id(manager_id)
                .date_embauche(date_embauche != null ? date_embauche : LocalDate.now())
                .actif(actif)
                .date_creation(date_creation != null ? date_creation : LocalDate.now())
                .build();

            Utilisateur saved = utilisateurRepository.save(pers1);

            if (email != null && !email.isEmpty()) {
                try {
                    emailService.sendWelcomeEmail(email, matricule, rawPassword);
                } catch (Exception ignored) {}
            }

            try {
                int annee = LocalDate.now().getYear();
                soldeCongeService.initialiserSoldesPourUtilisateur(saved.getId(), annee);
            } catch (Exception ignored) {}

            return "Utilisateur créé avec succès. Mot de passe généré : " + rawPassword;
        } else {
            return "Utilisateur déjà répertorié...";
        }
    }

    // --- Récupération ---
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id).orElse(null);
    }

    // --- Mise à jour ---
    public Utilisateur updateUtilisateur(Utilisateur details) {
        Utilisateur user = null;
        if (details.getId() != null) {
            user = utilisateurRepository.findById(details.getId()).orElse(null);
        }
        if (user == null && details.getMatricule() != null) {
            user = utilisateurRepository.findByMatricule(details.getMatricule()).orElse(null);
        }
        if (user == null) {
            return null;
        }

        if (details.getMatricule() != null && !details.getMatricule().isEmpty()) {
            user.setMatricule(details.getMatricule());
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
        if (details.getPassword() != null && !details.getPassword().isEmpty()) {
            String p = details.getPassword().trim();
            if (!p.startsWith("$2a$") && !p.startsWith("$2b$") && !p.startsWith("$2y$")) {
                user.setPassword(passwordEncoder.encode(p));
            } else {
                user.setPassword(p);
            }
        }
        if (details.getRole() != null) {
            user.setRole(details.getRole());
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