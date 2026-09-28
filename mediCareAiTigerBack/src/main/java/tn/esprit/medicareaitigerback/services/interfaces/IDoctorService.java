package tn.esprit.medicareaitigerback.services.interfaces;


import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import tn.esprit.medicareaitigerback.exceptions.AgentNotFoundException;
import tn.esprit.medicareaitigerback.exceptions.EmailUsedException;
import tn.esprit.medicareaitigerback.exceptions.PhoneNumberUsedException;
import tn.esprit.medicareaitigerback.exceptions.UnknownUserException;
import tn.esprit.medicareaitigerback.exceptions.UserDontMatchWithApplicationException;
import tn.esprit.medicareaitigerback.exceptions.UserNameUsedException;
import tn.esprit.medicareaitigerback.models.dto.request.CreateDoctorRequestDTO;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateClientDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.DoctorRespDTO;

import java.io.IOException;

public interface IDoctorService {
    void create(CreateDoctorRequestDTO dto) throws EmailUsedException, AgentNotFoundException;

    void edit(Long id, UpdateClientDTO dto) throws EmailUsedException, UserNameUsedException, PhoneNumberUsedException;

    Page<DoctorRespDTO> findAll(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty);

    Resource export(String queryValue) throws IOException, AgentNotFoundException;

    ApiRespDTO<Object> signIn(LoginDTO dto) throws UserDontMatchWithApplicationException, UnknownUserException;

    void delete(Long id) throws UnknownUserException;
}
