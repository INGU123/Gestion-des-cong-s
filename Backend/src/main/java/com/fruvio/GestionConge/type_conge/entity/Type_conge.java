package com.fruvio.GestionConge.type_conge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "type_conge")
public class Type_conge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;
    private String libelle;

    @Builder.Default
    private String regle_acquisition = "STANDARD";

    private String justification_requis;

    @Builder.Default
    private String couleur = "#2563eb";

    @Builder.Default
    private Boolean actif = true;

    private boolean justificatifObligatoire;

    @Builder.Default
    @Column(nullable = false)
    private Integer nombreJoursParAn = 0;
}