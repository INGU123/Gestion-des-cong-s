package com.fruvio.GestionConge.solde_conge.entity;

import java.sql.Timestamp;
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
@Table(name = "solde_conge")
@Entity
public class Solde_conge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long utilisateurId;
    @Column(name = "type_conge_id")
    private Long typeCongeId;
    private double soldeAquis;
    private double soldePris;
    private double soldeRestant;
    private int periode;
    private Timestamp date_maj;
    
}
