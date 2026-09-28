package tn.esprit.medicareaitigerback.services.interfaces;

import tn.esprit.medicareaitigerback.models.dto.request.CreateMedicamentDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateMedicamentDTO;
import tn.esprit.medicareaitigerback.models.dto.response.MedicamentResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Medicament;

import java.util.List;

public interface IMedicamentService {

    MedicamentResponseDTO create(CreateMedicamentDTO dto);

    List<MedicamentResponseDTO> findAll();

    MedicamentResponseDTO edit(Long id, UpdateMedicamentDTO dto);

    void delete(Long id);

    Medicament findEntityById(Long id);

    MedicamentResponseDTO updateStock(Long id, Integer quantity);
}

