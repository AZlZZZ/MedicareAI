package tn.esprit.medicareaitigerback.models.dto.mapper;

import tn.esprit.medicareaitigerback.models.dto.request.AddressAddDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AddressDTO;
import tn.esprit.medicareaitigerback.models.entities.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressDTO toDTO(Address address);
    Address toEntity(AddressAddDTO addressAddDTO);;
}

