package tn.esprit.medicareaitigerback.repositories;


import tn.esprit.medicareaitigerback.models.entities.Reasons;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ReasonsRepository extends JpaRepository<Reasons, Long>, JpaSpecificationExecutor<Reasons> {
}
