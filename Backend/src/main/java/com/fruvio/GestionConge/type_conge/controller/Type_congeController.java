package com.fruvio.GestionConge.type_conge.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.fruvio.GestionConge.type_conge.entity.Type_conge;
import com.fruvio.GestionConge.type_conge.service.Type_congeService;

@RestController
@RequestMapping("/type-conge")
@CrossOrigin(origins = "http://localhost:3000")
@PreAuthorize("hasRole('ADMIN')")
public class Type_congeController {

    private final Type_congeService typeCongeService;

    public Type_congeController(Type_congeService typeCongeService) {
        this.typeCongeService = typeCongeService;
    }

    // Créer un nouveau type de congé (Admin)
    @PostMapping("/create")
    public ResponseEntity<Type_conge> createType_conge(@RequestBody Type_conge typeConge) {
        Type_conge nouveauType = typeCongeService.save(typeConge);
        return new ResponseEntity<>(nouveauType, HttpStatus.CREATED);
    }

    // Récupérer tous les types de congés
    @GetMapping("/all")
    public ResponseEntity<List<Type_conge>> getAllTypes() {
        return ResponseEntity.ok(typeCongeService.findAll());
    }

    // Récupérer un type par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Type_conge> getTypeById(@PathVariable Long id) {
        Type_conge type = typeCongeService.findById(id);
        if (type != null) {
            return ResponseEntity.ok(type);
        }
        return ResponseEntity.notFound().build();
    }

    // Modifier un type de congé (ex: changer le nombre de jours alloués par an)
    @PutMapping("/update/{id}")
    public ResponseEntity<Type_conge> updateType_conge(@PathVariable Long id, @RequestBody Type_conge details) {
        Type_conge misAJour = typeCongeService.update(id, details);
        if (misAJour != null) {
            return ResponseEntity.ok(misAJour);
        }
        return ResponseEntity.notFound().build();
    }

    // Supprimer un type de congé
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteType_conge(@PathVariable Long id) {
        if (typeCongeService.deleteById(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}