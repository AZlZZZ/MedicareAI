package tn.esprit.medicareaitigerback.models.dto.mapper;

import tn.esprit.medicareaitigerback.models.dto.response.RoleDTO;
import tn.esprit.medicareaitigerback.models.dto.response.RoleRespDTO;
import tn.esprit.medicareaitigerback.models.entities.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")

public interface RoleMapper {

    RoleDTO toRoleDTO(Role role);

    RoleRespDTO toRoleRespDTO(Role role);
}

