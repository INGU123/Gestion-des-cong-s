package com.fruvio.GestionConge.utilisateur.service;

import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.repository.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final UtilisateurRepository utilisateurRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UtilisateurRepository utilisateurRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Génère un token de réinitialisation et envoie un email avec le lien.
     */
    public void createPasswordResetToken(String email) {
        Utilisateur user = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email inconnu"));

        // Génération du token unique
        String token = UUID.randomUUID().toString();
        user.setResetToken(token);
        user.setTokenExpiration(LocalDateTime.now().plusMinutes(30)); // expire dans 30 min
        utilisateurRepository.save(user);

        // Lien de réinitialisation
        String resetLink = "http://localhost:8080/auth/reset-password?token=" + token;

        // Envoi de l'email
        emailService.sendEmail(
                user.getEmail(),
                "Réinitialisation de votre mot de passe",
                "Bonjour " + user.getNom() + ",\n\n" +
                "Cliquez sur ce lien pour réinitialiser votre mot de passe : " + resetLink + "\n\n" +
                "Ce lien expirera dans 30 minutes."
        );
    }

    /**
     * Réinitialise le mot de passe si le token est valide et non expiré.
     */
    public void resetPassword(String token, String newPassword) {
        Utilisateur user = utilisateurRepository.findByResetToken(token)
                .orElseThrow(() -> new RuntimeException("Token invalide"));

        if (user.getTokenExpiration() == null || user.getTokenExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Token expiré");
        }

        // Mise à jour du mot de passe
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        user.setTokenExpiration(null);
        utilisateurRepository.save(user);
    }
}
