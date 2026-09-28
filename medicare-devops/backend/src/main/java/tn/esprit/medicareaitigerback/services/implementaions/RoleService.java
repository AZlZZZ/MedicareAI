package tn.esprit.medicareaitigerback.services.implementaions;


import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.response.RoleResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Role;
import tn.esprit.medicareaitigerback.repositories.*;
import tn.esprit.medicareaitigerback.services.interfaces.IRoleService;
import tn.esprit.medicareaitigerback.specification.SearchCriteria;
import tn.esprit.medicareaitigerback.specification.SpecificationsBuilder;
import com.google.gson.Gson;;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@AllArgsConstructor
public class RoleService implements IRoleService {
    private RoleRepository roleRepository;
    // private RestTemplate restTemplate;
    private AgentRepository agentRepository;

    @Override
    public Page<RoleResponseDTO> getAllRole(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty) {
        Specification<Role> spec = new SpecificationsBuilder<Role>(
                Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class))
        ).build();
        Page<Role> rolePage = roleRepository.findAll(spec, PageRequest.of(pageIndex, numberPerPage, sortDirection, sortProperty));


        return rolePage.map(role -> {
            RoleResponseDTO dto = new RoleResponseDTO();
            dto.setId(role.getId());
            dto.setName(role.getName());

            return dto;
        });
    }



    @Override
    @Transactional
    public void createRole(String name) throws RoleAlreadyExistException {
        if (roleRepository.findByNameIgnoreCase(name).isPresent()) {
            throw new RoleAlreadyExistException("Role '" + name + "' already exists.");
        }
        roleRepository.save(new Role(name));
    }

    @Override
    @Transactional
    public void updateRoleName(Long roleId, String name) throws RoleNotFoundException {
        var role = roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException("role not found :" + roleId));
        roleRepository.findByNameIgnoreCase(name)
                .filter(existingRole -> !existingRole.getId().equals(roleId))
                .ifPresent(existing -> {
                    try {
                        throw new RoleAlreadyExistException("Role name '" + name + "' is already in use.");
                    } catch (RoleAlreadyExistException e) {
                        throw new RuntimeException(e);
                    }
                });
        role.setName(name);
        roleRepository.save(role);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) throws RoleNotFoundException, RoleInUseException {
        var role = roleRepository.findById(roleId).orElseThrow(() -> new RoleNotFoundException("role not found :" + roleId));
        boolean isRoleUsed = agentRepository.existsByRole(role);
        if (isRoleUsed) {
            throw new RoleInUseException("Cannot delete role '" + role.getName() + "' because it is still assigned to one or more agents.");
        }
        roleRepository.delete(role);
    }


}
