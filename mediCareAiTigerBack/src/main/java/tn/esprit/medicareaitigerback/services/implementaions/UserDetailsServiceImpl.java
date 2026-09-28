package tn.esprit.medicareaitigerback.services.implementaions;

import tn.esprit.medicareaitigerback.exceptions.PasswordNotInitilizedException;
import tn.esprit.medicareaitigerback.exceptions.UserNotEnabledException;
import tn.esprit.medicareaitigerback.models.dto.business.AuthDTO;
import tn.esprit.medicareaitigerback.models.entities.User;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.repositories.AgentRepository;
import tn.esprit.medicareaitigerback.repositories.PatientRepository;
import tn.esprit.medicareaitigerback.repositories.UserRepository;
import tn.esprit.medicareaitigerback.security.SecurityUtils;
import com.google.gson.Gson;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@NoArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SecurityUtils securityUtils;
    @Autowired
    PatientRepository clientRepository;
    @Autowired
    tn.esprit.medicareaitigerback.repositories.DoctorRepository doctorRepository;

    @Override
    public UserDetails loadUserByUsername(String dto) throws UsernameNotFoundException {
        var authDTO = new Gson().fromJson(dto, AuthDTO.class);
        User user;

        switch (authDTO.getUserType()) {
            case AGENT:
                user = agentRepository.findAgentByUserNameWithRole(authDTO.getUsername()).orElse(null);
                break;
            case PATIENT:
                user = clientRepository.findClientByUserName(authDTO.getUsername()).orElse(null);
                break;
            case DOCTOR:
                user = doctorRepository.findByUserName(authDTO.getUsername()).orElse(null);
                break;
            default:
                user = agentRepository.findAgentByUserName(authDTO.getUsername()).orElse(null);
                break;
        }


        if (user == null) {
            throw new UsernameNotFoundException(
                    "User Not Found with userName: " + authDTO.getUsername()
            );
        }
        if (user.getState().equals(UserState.BLOCKED) && user.getUnblockDate() != null && LocalDate.now().isAfter(user.getUnblockDate())) {
            user.setState(UserState.ENABLED);
            userRepository.save(user);
        }

        switch (authDTO.getUserType()) {
            case AGENT:
            case PATIENT:
            case DOCTOR:
            default:
                if (user.getState() != UserState.ENABLED) {
                    try {
                        throw new UserNotEnabledException("User Not Enabled: " + authDTO.getUsername());
                    } catch (UserNotEnabledException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
        }


        if (user.getPassword() == null) {
            try {
                throw new PasswordNotInitilizedException("Password Not Initilized: " + authDTO.getUsername());
            } catch (PasswordNotInitilizedException e) {
                throw new RuntimeException(e);
            }
        }


        return securityUtils.build(user, authDTO.getUserType());
    }
}
