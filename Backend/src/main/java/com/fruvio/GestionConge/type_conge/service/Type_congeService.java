package com.fruvio.GestionConge.type_conge.service;

import java.util.List;
import org.springframework.stereotype.Service;
import com.fruvio.GestionConge.type_conge.entity.Type_conge;
import com.fruvio.GestionConge.type_conge.repository.Type_congeRepository;

@Service
public class Type_congeService {

    private final Type_congeRepository Type_congeRepository;

    public Type_congeService(Type_congeRepository Type_congeRepository) {
        this.Type_congeRepository = Type_congeRepository;
    }

    public Type_conge save(Type_conge Type_conge) {
        return Type_congeRepository.save(Type_conge);
    }

    public List<Type_conge> findAll() {
        return Type_congeRepository.findAll();
    }

    public Type_conge findById(Long id) {
        return Type_congeRepository.findById(id).orElse(null);
    }

    public Type_conge update(Long id, Type_conge details) {
        Type_conge existant = findById(id);
        if (existant != null) {
            existant.setLibelle(details.getLibelle());
            existant.setNombreJoursParAn(details.getNombreJoursParAn());
            existant.setJustificatifObligatoire(details.isJustificatifObligatoire());
            return Type_congeRepository.save(existant);
        }
        return null;
    }

    public boolean deleteById(Long id) {
        if (Type_congeRepository.existsById(id)) {
            Type_congeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
