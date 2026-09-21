package com.fruvio.GestionConge.solde_conge.service;

import java.sql.Timestamp;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fruvio.GestionConge.solde_conge.entity.Solde_conge;
import com.fruvio.GestionConge.solde_conge.repository.Solde_congeRepository;
import com.fruvio.GestionConge.type_conge.entity.Type_conge;
import com.fruvio.GestionConge.type_conge.repository.Type_congeRepository;

@Service
public class Solde_congeService {

    private final Solde_congeRepository soldeCongeRepository;
    private final Type_congeRepository typeCongeRepository;

    public Solde_congeService(Solde_congeRepository soldeCongeRepository, Type_congeRepository typeCongeRepository) {
        this.soldeCongeRepository = soldeCongeRepository;
        this.typeCongeRepository = typeCongeRepository;
    }

    // Récupérer tous les soldes d’un utilisateur (avec auto-initialisation si vide)
    public List<Solde_conge> getSoldesParUtilisateur(Long utilisateurId) {
        List<Solde_conge> soldes = soldeCongeRepository.findByUtilisateurId(utilisateurId);
        if (soldes.isEmpty()) {
            int annee = java.time.LocalDate.now().getYear();
            initialiserSoldesPourUtilisateur(utilisateurId, annee);
            soldes = soldeCongeRepository.findByUtilisateurId(utilisateurId);
        }
        return soldes;
    }

    // Récupérer tous les soldes
    public List<Solde_conge> getAllSoldes() {
        return soldeCongeRepository.findAll();
    }

    // Initialiser les soldes annuels pour un utilisateur
    @Transactional
    public void initialiserSoldesPourUtilisateur(Long utilisateurId, int annee) {
        List<Type_conge> types = typeCongeRepository.findAll();

        for (Type_conge type : types) {
            if (!soldeCongeRepository.existsByUtilisateurIdAndTypeCongeIdAndPeriode(utilisateurId, type.getId(), annee)) {
                Solde_conge solde = new Solde_conge();
                solde.setUtilisateurId(utilisateurId);
                solde.setTypeCongeId(type.getId());
                solde.setPeriode(annee);
                int nbJours = (type.getNombreJoursParAn() != null) ? type.getNombreJoursParAn() : 0;
                solde.setSoldeAquis(nbJours);
                solde.setSoldePris(0);
                solde.setSoldeRestant(nbJours);
                solde.setDate_maj(new Timestamp(System.currentTimeMillis()));

                soldeCongeRepository.save(solde);
            }
        }
    }

    // Ajuster manuellement le solde (réservé à l'Admin)
    @Transactional
    public Solde_conge ajusterSolde(Long soldeId, int nouveauNombreJoursRestants) {
        Solde_conge solde = soldeCongeRepository.findById(soldeId)
                .orElseThrow(() -> new IllegalArgumentException("Solde introuvable"));

        solde.setSoldeRestant(nouveauNombreJoursRestants);
        solde.setDate_maj(new Timestamp(System.currentTimeMillis()));

        return soldeCongeRepository.save(solde);
    }
}
