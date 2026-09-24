package com.fruvio.GestionConge.utilisateur.dto;

import java.time.LocalDate;

import com.fruvio.GestionConge.utilisateur.entity.Role;
import com.fruvio.GestionConge.utilisateur.entity.Utilisateur;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurResponse {

    private Long id;
    private String matricule;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private boolean actif;
    private Long service_id;
    private Long manager_id;
    private LocalDate date_embauche;
    private LocalDate date_creation;

    public static UtilisateurResponse fromEntity(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        String roleName = utilisateur.getRole() != null
                ? utilisateur.getRole().name()
                : Role.EMPLOYE.name();

        return UtilisateurResponse.builder()
                .id(utilisateur.getId())
                .matricule(utilisateur.getMatricule())
                .nom(utilisateur.getNom())
                .prenom(utilisateur.getPrenom())
                .email(utilisateur.getEmail())
                .role(roleName)
                .actif(utilisateur.isActif())
                .service_id(utilisateur.getService_id())
                .manager_id(utilisateur.getManager_id())
                .date_embauche(utilisateur.getDate_embauche())
                .date_creation(utilisateur.getDate_creation())
                .build();
    }
}
