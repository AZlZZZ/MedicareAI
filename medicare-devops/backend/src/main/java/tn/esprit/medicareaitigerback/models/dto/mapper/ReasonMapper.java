package tn.esprit.medicareaitigerback.models.dto.mapper;

import tn.esprit.medicareaitigerback.models.dto.response.ReasonResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Reasons;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ReasonMapper {
    ReasonResponseDTO mapToDTO(Reasons reasons);
}
