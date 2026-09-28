package tn.esprit.medicareaitigerback;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tn.esprit.medicareaitigerback.models.entities.Agent;
import tn.esprit.medicareaitigerback.models.entities.Role;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.repositories.AgentRepository;
import tn.esprit.medicareaitigerback.repositories.ReasonsRepository;
import tn.esprit.medicareaitigerback.repositories.RoleRepository;
import tn.esprit.medicareaitigerback.utilities.Log;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableTransactionManagement
@EnableAspectJAutoProxy
@EnableScheduling
public class MediCareAiTigerBackApplication extends AbstractSecurityWebApplicationInitializer implements CommandLineRunner {
    private final Environment environment;
    private final AgentRepository agentRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ReasonsRepository reasonsRepository;


    public MediCareAiTigerBackApplication(Environment environment, AgentRepository agentRepository,
                                  RoleRepository roleRepository,

                                  ReasonsRepository reasonsRepository) {

        this.environment = environment;
        this.agentRepository = agentRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.reasonsRepository = reasonsRepository;
    }

    public static void main(String[] args) {
        Log.instance.i("Start Running.....");
        SpringApplication.run(MediCareAiTigerBackApplication.class, args);
        Log.instance.i("End Running:::::");
    }


    @Override
    public void run(String... args) throws Exception {
        Log.instance.i("InitData...");
        initData();
    }
    private void initData() {
        if (agentRepository.findAll().isEmpty()) {
            Agent superAdmin = new Agent();
            superAdmin.setFirstName("administrationAccount");
            superAdmin.setLastName("administrationAccount");
            superAdmin.setUserName("admin");
            superAdmin.setEmail("admin@medicare.tn");
            superAdmin.setPassword(passwordEncoder.encode("Admin@1234!"));
            superAdmin.setState(UserState.ENABLED);
            var role1 = new Role("SUPER_ADMIN");
            var role2 = new Role("ADMIN");
            var role3 = new Role("AGENT");

            roleRepository.save(role1);
            roleRepository.save(role2);
            roleRepository.save(role3);
            superAdmin.setRole(role1);
            agentRepository.save(superAdmin);
        }
    }
}
