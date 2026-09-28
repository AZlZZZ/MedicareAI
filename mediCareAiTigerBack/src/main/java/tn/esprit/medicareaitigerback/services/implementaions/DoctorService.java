package tn.esprit.medicareaitigerback.services.implementaions;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import tn.esprit.medicareaitigerback.exceptions.AgentNotFoundException;
import tn.esprit.medicareaitigerback.exceptions.EmailUsedException;
import tn.esprit.medicareaitigerback.exceptions.PhoneNumberUsedException;
import tn.esprit.medicareaitigerback.exceptions.UnknownUserException;
import tn.esprit.medicareaitigerback.exceptions.UserDontMatchWithApplicationException;
import tn.esprit.medicareaitigerback.exceptions.UserNameUsedException;
import tn.esprit.medicareaitigerback.models.dto.business.AuthDTO;
import tn.esprit.medicareaitigerback.models.dto.mapper.DoctorMapper;
import tn.esprit.medicareaitigerback.models.dto.request.CreateDoctorRequestDTO;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateClientDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.DoctorRespDTO;
import tn.esprit.medicareaitigerback.models.entities.Address;
import tn.esprit.medicareaitigerback.models.entities.Doctor;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import tn.esprit.medicareaitigerback.repositories.AddressRepository;
import tn.esprit.medicareaitigerback.repositories.DoctorRepository;
import tn.esprit.medicareaitigerback.security.SecurityUtils;
import tn.esprit.medicareaitigerback.security.UserDetailsImpl;
import tn.esprit.medicareaitigerback.services.interfaces.*;
import tn.esprit.medicareaitigerback.specification.SearchCriteria;
import tn.esprit.medicareaitigerback.specification.SpecificationsBuilder;
import tn.esprit.medicareaitigerback.utilities.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;

@Service
@AllArgsConstructor
public class DoctorService implements IDoctorService {
    private DoctorRepository repository;
    private DoctorMapper doctorMapper;
    private AddressRepository addressRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private AuthenticationManager authenticationManager;
    private SecurityUtils securityUtils;

    @Override
    public void create(CreateDoctorRequestDTO dto) throws EmailUsedException, AgentNotFoundException {

        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new RuntimeException("Email is required");
        }

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new RuntimeException("Password is required");
        }

        if (dto.getFirstName() == null || dto.getLastName() == null) {
            throw new RuntimeException("Name is required");
        }
        if (repository.findByEmail(dto.getEmail()).isPresent()) {
            throw new EmailUsedException("Email used:" + dto.getEmail());
        }
        Address homeAddr = new Address();
        homeAddr.setAddress(dto.getHomeAddress());
        homeAddr.setLatitude(dto.getHomeLatitude());
        homeAddr.setLongitude(dto.getHomeLongitude());
        addressRepository.save(homeAddr);
        Doctor patient = new Doctor();
        patient.setFirstName(dto.getFirstName());
        patient.setLastName(dto.getLastName());
        patient.setEmail(dto.getEmail());
        patient.setUserName(dto.getEmail());
        patient.setPassword(passwordEncoder.encode(dto.getPassword()));
        patient.setPhoneNumber(dto.getPhoneNumber());
        patient.setSpecialization(dto.getSpecialization());

        patient.setState(UserState.ENABLED);
        repository.save(patient);
    }

    @Override
    public void edit(Long id, UpdateClientDTO dto) throws EmailUsedException, UserNameUsedException, PhoneNumberUsedException {

    }

    @Override
    public Page<DoctorRespDTO> findAll(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty) {
        Specification<Doctor> spec = new SpecificationsBuilder<Doctor>(
                Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class))
        ).build();
        Page<Doctor> doctorPage = repository.findAll(spec, PageRequest.of(pageIndex, numberPerPage, sortDirection, sortProperty));
        return doctorPage.map(doctorMapper::mapToDTO);
    }

    @Override
    public Resource export(String queryValue) throws IOException, AgentNotFoundException {

        var queryResult = repository.findAll(new SpecificationsBuilder<Doctor>(Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class))).build());

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
                "Specialization" +
                ";" +
                "Statut" +
                ";" +
                "\n";
        writer.write(header);
        Log.instance.i(".....", String.valueOf(queryResult.size()));
        for (Doctor item : queryResult) {
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
            if (item.getSpecialization() == null)
                line.append("-");
            else
                line.append(item.getSpecialization());
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

    @Override
    public void delete(Long id) throws UnknownUserException {
        if (!repository.existsById(id)) {
            throw new UnknownUserException("Doctor not found: " + id);
        }
        repository.deleteById(id);
    }

    @Override
    public ApiRespDTO<Object> signIn(LoginDTO dto) throws UserDontMatchWithApplicationException, UnknownUserException {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            new Gson().toJson(new AuthDTO(dto.getUsername(), UserType.DOCTOR)),
                            dto.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ApiRespDTO<>(301, "Error username or password", null)).getBody();
        }
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        if (userPrincipal.getUserType() == UserType.DOCTOR) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // Use generateJwtToken2 for consistent access+refresh token structure
            Map<String, String> tokens = securityUtils.generateJwtToken2(authentication, UserType.DOCTOR, null);
            // Persist refresh token
            repository.findByUserName(userPrincipal.getUsername()).ifPresent(doctor -> {
                doctor.setRefreshToken(tokens.get("refreshToken"));
                repository.save(doctor);
            });
            return new ApiRespDTO<>(200, "Success", tokens);
        } else {
            throw new UserDontMatchWithApplicationException("User does not match the application");
        }
    }
}
