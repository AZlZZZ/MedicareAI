package tn.esprit.medicareaitigerback.security;


import tn.esprit.medicareaitigerback.models.entities.User;
import tn.esprit.medicareaitigerback.repositories.AgentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.medicareaitigerback.repositories.PatientRepository;

import java.util.Optional;

public class SpringSecurityAuditorAware implements AuditorAware<User> {

    @Autowired
    AgentRepository agentRepository;
    @Autowired
    PatientRepository clientRepository;
    @Autowired
    tn.esprit.medicareaitigerback.repositories.DoctorRepository doctorRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<User> getCurrentAuditor() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Optional<User> appUser;
        switch (((UserDetailsImpl) authentication.getPrincipal()).getUserType()) {
            case PATIENT:
                appUser = clientRepository.findUserByUserName(((UserDetailsImpl) authentication.getPrincipal()).getUsername());
                break;
            case AGENT:
                appUser = agentRepository.findUserByUserName(((UserDetailsImpl) authentication.getPrincipal()).getUsername());
                break;
            case DOCTOR:
                appUser = doctorRepository.findUserByUserName(((UserDetailsImpl) authentication.getPrincipal()).getUsername());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + ((UserDetailsImpl) authentication.getPrincipal()).getUserType());
        }

        return appUser;

    }
}