package com.fruvio.GestionConge.utilisateur.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "utilisateur")
@Entity
public class Utilisateur implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String matricule;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    @Pattern(
        regexp="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$",
        message="Email invalide"
    )
    private String email;

    @Column(nullable = false)
    private String password;

    private Long service_id;
    private Long manager_id;

    @Column(nullable = false)
    private LocalDate date_embauche;

    @Builder.Default
    private boolean actif = true;

    @Column(nullable = false)
    private LocalDate date_creation;

    @Enumerated(EnumType.STRING)
    private Role role;

    // Champs pour la récupération de mot de passe
    @Column(name = "reset_token")
    private String resetToken;

    @Column(name = "token_expiration")
    private LocalDateTime tokenExpiration;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    public void setMotDePass(String motDePass) {
        this.password = motDePass;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() { 
        return password; 
    }

    @Override
    public String getUsername() { 
        return matricule; 
    }

    @Override
    public boolean isAccountNonExpired() { 
        return true; 
    }

    @Override
    public boolean isAccountNonLocked() { 
        return true; 
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }

    @Override
    public boolean isEnabled() { 
        return actif; 
    }
}