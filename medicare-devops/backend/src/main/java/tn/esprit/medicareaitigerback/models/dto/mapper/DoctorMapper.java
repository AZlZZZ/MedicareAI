package tn.esprit.medicareaitigerback.models.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tn.esprit.medicareaitigerback.models.dto.response.ClientRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ClientResponseDTO;
import tn.esprit.medicareaitigerback.models.dto.response.DoctorRespDTO;
import tn.esprit.medicareaitigerback.models.entities.Doctor;
import tn.esprit.medicareaitigerback.models.entities.Patient;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(target = "profilePhoto", source = "doctor", qualifiedByName = "mapProfilePhoto")
    DoctorRespDTO mapToDTO(Doctor doctor);

    // Custom logic for the profile photo path string
    @Named("mapProfilePhoto")
    default String mapProfilePhoto(Doctor doctor) {
        if (doctor.getProfilePicture() == null) {
            return null;
        }
        return doctor.getProfilePicture().getFileDirectory() + doctor.getProfilePicture().getFileName();
    }
}
