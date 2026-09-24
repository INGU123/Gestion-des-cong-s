package com.fruvio.GestionConge.utilisateur.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class ChangePasswordRequest {

    @NotBlank(message = "L'ancien mot de passe est obligatoire.")
    private String ancienMotDePasse;

    @NotBlank(message = "Le nouveau mot de passe est obligatoire.")
    @Size(min = 6, message = "Le nouveau mot de passe doit comporter au moins 6 caractères.")
    private String nouveauMotDePasse;

    private String confirmation;

    @JsonProperty("oldPassword")
    public void setOldPassword(String oldPassword) {
        if (this.ancienMotDePasse == null || this.ancienMotDePasse.isBlank()) {
            this.ancienMotDePasse = oldPassword;
        }
    }

    @JsonProperty("newPassword")
    public void setNewPassword(String newPassword) {
        if (this.nouveauMotDePasse == null || this.nouveauMotDePasse.isBlank()) {
            this.nouveauMotDePasse = newPassword;
        }
    }
}
