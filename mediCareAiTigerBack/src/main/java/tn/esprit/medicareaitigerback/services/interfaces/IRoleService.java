package tn.esprit.medicareaitigerback.services.interfaces;

import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.response.RoleResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;


public interface IRoleService {

    Page<RoleResponseDTO> getAllRole(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty);

    void createRole(String name) throws RoleAlreadyExistException;

    void updateRoleName(Long roleId, String name) throws RoleNotFoundException;

    void deleteRole(Long roleId) throws RoleNotFoundException, RoleInUseException;
}
