package tn.esprit.medicareaitigerback.services.implementaions;


import tn.esprit.medicareaitigerback.models.entities.Attachment;
import tn.esprit.medicareaitigerback.models.enumeration.AttachmentType;
import tn.esprit.medicareaitigerback.services.interfaces.IAttachmentService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AttachmentService implements IAttachmentService {


    @Override
    public Attachment createAttachment(String fileName, String fileType, String fileDirectory, AttachmentType type, String description) {
        Attachment attachment = new Attachment();
        attachment.setFileName(fileName);
        attachment.setFileType(fileType);
        attachment.setFileDirectory(fileDirectory);
        attachment.setType(type);
        attachment.setDescription(description);
        return attachment;
    }

}
