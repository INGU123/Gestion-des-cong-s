package com.fruvio.GestionConge.type_conge.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;

@NonNull
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Type_conge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String libelle;
    private String regle_acquisition;
    private String justification_requis;
    private String couleur;
    private Boolean actif;
    @ManyToOne
    @JoinColumn(name = "type_conge_id", referencedColumnName = "id")
    private Type_conge typeConge;
    private boolean justificatifObligatoire;
    @Column(nullable = false)
    private Integer nombreJoursParAn=0;
}