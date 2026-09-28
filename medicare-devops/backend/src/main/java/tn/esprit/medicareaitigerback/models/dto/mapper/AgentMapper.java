package tn.esprit.medicareaitigerback.models.dto.mapper;

import tn.esprit.medicareaitigerback.models.dto.request.RegisterAgentDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AgentDetails;
import tn.esprit.medicareaitigerback.models.dto.response.AgentRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AgentResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Agent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface AgentMapper {

    AgentRespDTO mapToDTO(Agent agent);

    AgentResponseDTO mapToDTO2(Agent agent);

    @Named("basicAgent")
    @Mapping(target = "roleName", source = "role.name")
    @Mapping(target = "lastName", source = "agent.lastName")
    @Mapping(target = "firstName", source = "agent.firstName")
    AgentDetails toDtoBasic(Agent agent);
    Agent fromRegisterAgentDTO(RegisterAgentDTO dto);

    default Date parseBirthDate(String birthDate) {
        if (birthDate == null) return null;
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(birthDate);
        } catch (ParseException e) {
            return null;
        }
    }
}

