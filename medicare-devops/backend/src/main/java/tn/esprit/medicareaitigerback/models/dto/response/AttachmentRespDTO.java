package tn.esprit.medicareaitigerback.models.dto.response;

import tn.esprit.medicareaitigerback.models.enumeration.AttachmentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentRespDTO {
    private Long id;
    private String fileName;
    private String fileDirectory;
    private String fileType;
    private AttachmentType type;
    private String description;
}
