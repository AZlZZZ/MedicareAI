package tn.esprit.medicareaitigerback.models.dto.mapper;

import  tn.esprit.medicareaitigerback.models.dto.response.AttachmentRespDTO;
import  tn.esprit.medicareaitigerback.models.entities.Attachment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AttachmentMapper {
    AttachmentRespDTO mapToDto(Attachment attachment);
}
