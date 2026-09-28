package tn.esprit.medicareaitigerback.services.interfaces;

import org.springframework.core.io.Resource;
import org.springframework.security.access.annotation.Secured;
import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.request.*;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ClientRespDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import tn.esprit.medicareaitigerback.models.entities.Patient;

import java.io.IOException;

public interface IPatientService {
    void createPatient(CreatePatientRequestDTO dto) throws EmailUsedException, AgentNotFoundException;

    void edit(Long idClient, UpdateClientDTO dto) throws EmailUsedException, UserNameUsedException, PhoneNumberUsedException;


    Page<ClientRespDTO> findAllClient(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty);

    ClientRespDTO findById(Long clientId) throws PatientNotFoundException;

    ApiRespDTO<Object> requestAccountClient(RequestPatientDTO dto) throws UserNameUsedException;

    ApiRespDTO<Object> signIn(LoginDTO dto) throws UnknownUserException, PasswordExpiredException, UserDontMatchWithApplicationException;

    @Secured({"ROLE_PATIENT"})
    void deleteAccount(String deleteCause) throws UnknownUserException;
    Patient findByUserName(String userName) throws UnknownUserException;
    Patient findByPseudo(String userName) throws UnknownUserException;
    @Secured({"ROLE_AGENT"})
    Resource export(String queryValue) throws IOException, AgentNotFoundException;

}
