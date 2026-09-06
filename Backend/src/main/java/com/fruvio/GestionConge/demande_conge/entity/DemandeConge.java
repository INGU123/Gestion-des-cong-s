package com.fruvio.GestionConge.demande_conge.entity;

import java.sql.Date;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "demande_conge")
public class DemandeConge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long utilisateurId; // celui qui demande

    @Column(name = "type_de_conge_id")
    private Long typeCongeId;
    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate dateDebut;

    @JsonFormat(pattern = "MM/dd/yyyy")
    private LocalDate dateFin;

    private String commentaire; // motif du demandeur

    private String statut; // EN_ATTENTE, VALIDEE, REFUSEE, ANNULEE

    @Column(name = "motif_refus")
    private String motifRefus; // rempli par le manager

    @Column(name = "validee_par")
    private Long valideePar; // manager qui traite

    private Date dateValidation;
    private Date dateCreation;
}
