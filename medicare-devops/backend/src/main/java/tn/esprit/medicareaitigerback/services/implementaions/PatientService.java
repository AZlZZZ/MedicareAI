package tn.esprit.medicareaitigerback.services.implementaions;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import tn.esprit.medicareaitigerback.config.AppUrlProperties;
import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.business.AuthDTO;
import tn.esprit.medicareaitigerback.models.dto.mapper.PatientMapper;
import tn.esprit.medicareaitigerback.models.dto.request.*;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ClientRespDTO;
import tn.esprit.medicareaitigerback.models.entities.*;
import tn.esprit.medicareaitigerback.models.enumeration.*;
import tn.esprit.medicareaitigerback.repositories.*;
import tn.esprit.medicareaitigerback.security.SecurityUtils;
import tn.esprit.medicareaitigerback.security.UserDetailsImpl;
import tn.esprit.medicareaitigerback.services.interfaces.*;
import tn.esprit.medicareaitigerback.specification.SearchCriteria;
import tn.esprit.medicareaitigerback.specification.SpecificationsBuilder;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.medicareaitigerback.utilities.Log;
import tn.esprit.medicareaitigerback.utilities.Utils;
import org.apache.commons.validator.routines.EmailValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
@AllArgsConstructor
public class PatientService implements IPatientService {

    private PatientRepository patientRepository;
    private AddressRepository addressRepository;
    private PatientMapper patientMapper;

//    private ClientRepository clientRepository;
    private SecurityUtils securityUtils;
    private BCryptPasswordEncoder passwordEncoder;
//    private DeviceRepository deviceRepository;
    private AuthenticationManager authenticationManager;
    private IAttachmentService attachmentService;
    private AttachmentRepository attachmentRepository;
    private Utils utils;
    private IAgentService agentService;
    private IEmailService emailService;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private AppUrlProperties appUrlProperties;
    private AgentRepository agentRepository;
    @Override
    @Transactional
    public void createPatient(CreatePatientRequestDTO dto) throws EmailUsedException, AgentNotFoundException {

        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (dto.getFirstName() == null || dto.getLastName() == null) {
            throw new RuntimeException("Name is required");
        }
        if (patientRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailUsedException("Email used:" + dto.getEmail());
        }
        Address homeAddr = new Address();
        homeAddr.setAddress(dto.getHomeAddress());
        homeAddr.setLatitude(dto.getHomeLatitude());
        homeAddr.setLongitude(dto.getHomeLongitude());
         addressRepository.save(homeAddr);
        Patient patient = new Patient();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setUserName(dto.getEmail());
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setState(UserState.ENABLED);
        patientRepository.save(patient);
    }

    @Override
    public void edit(Long idClient, UpdateClientDTO dto) throws EmailUsedException, UserNameUsedException, PhoneNumberUsedException {
        var client = patientRepository.findById(idClient).orElseThrow(() -> new UsernameNotFoundException("Client not found" + idClient));
        if (!Objects.equals(client.getEmail(), dto.getEmail()) && isEmailUsed(dto.getEmail())) {
            throw new EmailUsedException("Email used:" + dto.getEmail());
        } else if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty() && !Objects.equals(client.getPhoneNumber(), dto.getPhoneNumber()) && isClientPhoneNumberUsed(dto.getPhoneNumber())) {
            throw new PhoneNumberUsedException("Phone number used:" + dto.getPhoneNumber());
        } else {
            client.fromDTO(dto);
            patientRepository.save(client);
        }

    }
    private Boolean isEmailUsed(String email) {
        return patientRepository.getClientByEmail(email) != null;
    }

    public Boolean isUserNameUsed(String driverName) {
        return patientRepository.getClientByUsername(driverName) != null;
    }
    public Boolean isClientPhoneNumberUsed(String phoneNumber) {
        return patientRepository.getClientByPhoneNumber(phoneNumber) != null;
    }

    @Override
    public Page<ClientRespDTO> findAllClient(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty) {
        Specification<Patient> spec = new SpecificationsBuilder<Patient>(
                Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class))
        ).build();
        Page<Patient> clientPage = patientRepository.findAll(spec, PageRequest.of(pageIndex, numberPerPage, sortDirection, sortProperty));
        return clientPage.map(patientMapper::mapToDTO);
    }

    @Override
    public ClientRespDTO findById(Long clientId) throws PatientNotFoundException {
        var client = patientRepository.findById(clientId).orElseThrow(() -> new PatientNotFoundException("Patient not found " + clientId));
        return patientMapper.mapToDTO(client);
    }


    @Override
    public Patient findByUserName(String userName) throws UnknownUserException {
        if (EmailValidator.getInstance().isValid(userName)) {
            return findByEmail(userName);
        } else {
            return findByPseudo(userName);
        }
    }

    @Override
    @Transactional
    public ApiRespDTO<Object> requestAccountClient(RequestPatientDTO dto) throws UserNameUsedException {
        var isNewUser = true;
        if (EmailValidator.getInstance().isValid(dto.getUsername()) &&
                patientRepository.findClientByEmail(dto.getUsername()).isPresent()) {
            isNewUser = false;
        } else if (patientRepository.findClientByUserName(dto.getUsername()).isPresent()) {
            isNewUser = false;
        }
        if (isNewUser) {

            Patient client = new Patient();
            client.setUserName(dto.getUsername());
            if (EmailValidator.getInstance().isValid(dto.getUsername())) {
                client.setEmail(dto.getUsername());
            } else {
                client.setPhoneNumber(dto.getUsername());
                client.setPhonePrefix(dto.getPhonePrefix());
            }
            client.setState(UserState.ENABLED);
            patientRepository.save(client);
            client.setPassword(passwordEncoder.encode(dto.getPassword()));
            // No expiry — patient sets their own password at registration
            patientRepository.save(client);
            return new ApiRespDTO<>(200, "Account created", null);

        } else {
            throw new UserNameUsedException("'email' or 'phone' arealy exist");
        }
    }

    @Override
    @Transactional
    public ApiRespDTO<Object> signIn(LoginDTO dto) throws UnknownUserException, PasswordExpiredException, UserDontMatchWithApplicationException {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(new Gson().toJson(new AuthDTO(dto.getUsername(), UserType.PATIENT)), dto.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ApiRespDTO<>(301, "Error username or passowrd", null)).getBody();
        }
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        if (userPrincipal.getUserType() == UserType.PATIENT) {
            var client = patientRepository.findClientByUserName(userPrincipal.getUsername())
                    .orElseThrow(() -> new UnknownUserException("Patient not found"));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            Map<String, String> tokens = securityUtils.generateJwtToken2(authentication, UserType.PATIENT, null);
            client.setRefreshToken(tokens.get("refreshToken"));
            patientRepository.save(client);
            return new ApiRespDTO<>(200, "Success", tokens);
        } else {
            throw new UserDontMatchWithApplicationException("user dont match with application");
        }
    }

    @Override
    public void deleteAccount(String deleteCause) throws UnknownUserException {
        var userSession = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var client = findByPseudo(userSession.getUsername());
        client.setState(UserState.DELETED);
        client.setPhoneNumber(generateAnonymizedPhoneNumber(client.getPhoneNumber()));
        client.setUserName(generateAnonymizedPhoneNumber(client.getUserName()));
        client.setDeleteCause(deleteCause);
        patientRepository.save(client);
    }
    private String generateAnonymizedPhoneNumber(String originalPhoneNumber) {

        return originalPhoneNumber + "-DELETED";
    }
    private Patient findByEmail(String email) throws UnknownUserException {
        return patientRepository.findClientByEmail(email).orElseThrow(() -> new UnknownUserException("Unknown User:" + email));
    }
    @Override
    public Patient findByPseudo(String userName) throws UnknownUserException {
        return patientRepository.findClientByUserName(userName).orElseThrow(() -> new UnknownUserException("Unknown User:" + userName));
    }
    @Override
    public Resource export(String queryValue) throws IOException, AgentNotFoundException {

        var queryResult = patientRepository.findAll(new SpecificationsBuilder<Patient>(Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class))).build());

        Log.instance.i("sqlFindAllQyerry:::::" + queryResult.size());


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        String header = "Nom" +
                ";" +
                "Prénom" +
                ";" +
                "User_name" +
                ";" +
                "Email" +
                ";" +
                "Statut" +
                ";" +
                "\n";
        writer.write(header);
        Log.instance.i(".....", String.valueOf(queryResult.size()));
        for (Patient item : queryResult) {
            StringBuilder line = new StringBuilder();
            if (item.getLastName() == null)
                line.append("-");
            else
                line.append(item.getLastName());
            line.append(";");
            if (item.getFirstName() == null)
                line.append("-");
            else
                line.append(item.getFirstName());
            line.append(";");
            if (item.getUserName() == null)
                line.append("-");
            else
                line.append(item.getUserName());
            line.append(";");
            if (item.getEmail() == null)
                line.append("-");
            else
                line.append(item.getEmail());
            line.append(";");
            if (item.getState() == null)
                line.append("-");
            else
                line.append(item.getState());
            line.append(";");
            line.append("\n");
            writer.write(line.toString());
        }
        writer.flush();
        writer.close();

        byte[] data = outputStream.toByteArray();
        return new ByteArrayResource(data);
    }

}
