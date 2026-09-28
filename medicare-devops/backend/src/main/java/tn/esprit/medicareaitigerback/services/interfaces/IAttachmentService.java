package tn.esprit.medicareaitigerback.services.interfaces;


import tn.esprit.medicareaitigerback.models.entities.Attachment;
import tn.esprit.medicareaitigerback.models.enumeration.AttachmentType;

public interface IAttachmentService {
    Attachment createAttachment(String fileName, String fileType, String fileDirectory, AttachmentType type, String description);

}
