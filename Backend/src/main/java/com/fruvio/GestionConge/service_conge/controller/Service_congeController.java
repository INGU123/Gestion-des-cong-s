package com.fruvio.GestionConge.service_conge.controller;

import com.fruvio.GestionConge.service_conge.entity.Services;
import com.fruvio.GestionConge.service_conge.repository.Service_congeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class Service_congeController {

    private final Service_congeRepository serviceRepository;

    public Service_congeController(Service_congeRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    // 🔹 Récupérer tous les services
    @GetMapping
    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    // 🔹 Récupérer un service par ID
    @GetMapping("/{id}")
    public ResponseEntity<Services> getServiceById(@PathVariable Long id) {
        return serviceRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Créer un nouveau service
    @PostMapping("/create")
    public Services createService(@RequestBody Services service) {
        return serviceRepository.save(service);
    }

    // 🔹 Mettre à jour un service existant
    @PutMapping("/{id}")
    public ResponseEntity<Services> updateService(@PathVariable Long id, @RequestBody Services updatedService) {
        return serviceRepository.findById(id)
                .map(service -> {
                    service.setNom(updatedService.getNom());
                    service.setResponsable_id(updatedService.getResponsable_id());
                    service.setEffectifMinimum(updatedService.getEffectifMinimum());
                    return ResponseEntity.ok(serviceRepository.save(service));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 Supprimer un service
    @DeleteMapping("/{id}")
public ResponseEntity<?> deleteService(@PathVariable Long id) {
    return serviceRepository.findById(id)
            .map(service -> {
                serviceRepository.delete(service);
                return ResponseEntity.noContent().build(); // 204 No Content
            })
            .orElse(ResponseEntity.notFound().build()); // 404 Not Found
}
}

