package tn.esprit.medicareaitigerback.models.dto.mapper;

import tn.esprit.medicareaitigerback.models.dto.response.ClientRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ClientResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import tn.esprit.medicareaitigerback.models.entities.Patient;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    // --- Mapping for ClientRespDTO ---
    @Mapping(target = "profilePhoto", source = "client", qualifiedByName = "mapProfilePhoto")
      ClientRespDTO mapToDTO(Patient client);

    // --- Mapping for DetailProfilDTO ---
//    @Mapping(target = "profilePhoto", source = "client", qualifiedByName = "mapProfilePhoto")
//    DetailProfilDTO mapToDTO2(Patient client);

    // --- Mapping for ClientResponseDTO ---
    @Mapping(target = "photo", source = "client", qualifiedByName = "mapProfilePhoto")
    ClientResponseDTO mapToDTO3(Patient client);

    // Custom logic for the profile photo path string
    @Named("mapProfilePhoto")
    default String mapProfilePhoto(Patient client) {
        if (client.getProfilePicture() == null) {
            return null;
        }
        return client.getProfilePicture().getFileDirectory() + client.getProfilePicture().getFileName();
    }
}
