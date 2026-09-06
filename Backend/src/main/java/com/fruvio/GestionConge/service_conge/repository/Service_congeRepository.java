package com.fruvio.GestionConge.service_conge.repository;

import com.fruvio.GestionConge.service_conge.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Service_congeRepository extends JpaRepository<Services, Long> {
    // Tu peux ajouter des méthodes personnalisées si besoin
}
