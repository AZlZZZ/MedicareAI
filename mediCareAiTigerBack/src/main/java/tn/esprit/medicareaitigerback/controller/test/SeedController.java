package tn.esprit.medicareaitigerback.controller.test;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import tn.esprit.medicareaitigerback.models.entities.Agent;
import tn.esprit.medicareaitigerback.models.entities.Role;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.repositories.AgentRepository;
import tn.esprit.medicareaitigerback.repositories.RoleRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/seed")
@AllArgsConstructor
public class SeedController {

    private final AgentRepository agentRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/admin")
    public Map<String, String> seedAdmin() {
        // Idempotent — skip if already exists
        if (agentRepository.findAgentByUserName("admin").isPresent()) {
            return Map.of("status", "already exists", "username", "admin", "password", "Admin@1234!");
        }
        Role role = roleRepository.findByNameIgnoreCase("SUPER_ADMIN")
                .orElseGet(() -> roleRepository.save(new Role("SUPER_ADMIN")));

        Agent agent = new Agent();
        agent.setFirstName("Admin");
        agent.setLastName("Admin");
        agent.setUserName("admin");
        agent.setEmail("admin@medicare.tn");
        agent.setPassword(passwordEncoder.encode("Admin@1234!"));
        agent.setState(UserState.ENABLED);
        agent.setRole(role);
        agentRepository.save(agent);

        return Map.of("status", "created", "username", "admin", "password", "Admin@1234!");
    }
}
