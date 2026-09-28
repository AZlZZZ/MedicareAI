package tn.esprit.medicareaitigerback.services.implementaions;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.medicareaitigerback.models.dto.request.CreateMedicamentDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateMedicamentDTO;
import tn.esprit.medicareaitigerback.models.dto.response.MedicamentResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Medicament;
import tn.esprit.medicareaitigerback.repositories.MedicamentRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IMedicamentService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MedicamentService implements IMedicamentService {

    private final MedicamentRepository medicamentRepository;

    @Override
    public MedicamentResponseDTO create(CreateMedicamentDTO dto) {
        if (medicamentRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new IllegalStateException("Medicament already exists: " + dto.getName());
        }

        Medicament medicament = new Medicament();
        medicament.setName(dto.getName().trim());
        medicament.setDescription(dto.getDescription());
        medicament.setStock(dto.getStock() != null ? dto.getStock() : 0);
        Medicament saved = medicamentRepository.save(medicament);
        return toDTO(saved);
    }

    @Override
    public List<MedicamentResponseDTO> findAll() {
        return medicamentRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public MedicamentResponseDTO edit(Long id, UpdateMedicamentDTO dto) {
        Medicament medicament = medicamentRepository.findById(id).orElseThrow(() -> new IllegalStateException("Medicament not found: " + id));
        medicament.setName(dto.getName().trim());
        medicament.setDescription(dto.getDescription());
        if (dto.getStock() != null) medicament.setStock(dto.getStock());
        return toDTO(medicamentRepository.save(medicament));
    }

    @Override
    public void delete(Long id) {
        if (!medicamentRepository.existsById(id)) {
            throw new IllegalStateException("Medicament not found: " + id);
        }
        medicamentRepository.deleteById(id);
    }

    @Override
    public Medicament findEntityById(Long id) {
        return medicamentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Medicament not found: " + id));
    }

    @Override
    public MedicamentResponseDTO updateStock(Long id, Integer quantity) {
        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Medicament not found: " + id));
        medicament.setStock(quantity);
        return toDTO(medicamentRepository.save(medicament));
    }

    private MedicamentResponseDTO toDTO(Medicament medicament) {
        return new MedicamentResponseDTO(medicament.getId(), medicament.getName(), medicament.getDescription(), medicament.getStock());
    }
}

