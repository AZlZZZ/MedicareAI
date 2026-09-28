package tn.esprit.medicareaitigerback.repositories;

import tn.esprit.medicareaitigerback.models.entities.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {
    boolean existsByNameIgnoreCase(String name);
}

