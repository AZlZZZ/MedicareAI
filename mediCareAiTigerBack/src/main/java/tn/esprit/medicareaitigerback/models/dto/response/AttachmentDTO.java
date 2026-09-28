package tn.esprit.medicareaitigerback.models.dto.response;

import  tn.esprit.medicareaitigerback.models.enumeration.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
    private Long id;
    private AttachmentType type;
    private byte[] data;
}