package com.fruvio.GestionConge.utilisateur.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;
import com.fruvio.GestionConge.utilisateur.repository.UtilisateurRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String matricule) throws UsernameNotFoundException {
        if (matricule == null || matricule.trim().isEmpty()) {
            throw new UsernameNotFoundException("Matricule non fourni");
        }

        Utilisateur utilisateur = utilisateurRepository.findByMatricule(matricule.trim())
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le matricule: " + matricule));

        return new CustomUserDetails(utilisateur);
    }
}
