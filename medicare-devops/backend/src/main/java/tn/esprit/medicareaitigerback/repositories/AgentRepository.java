package tn.esprit.medicareaitigerback.repositories;


import tn.esprit.medicareaitigerback.models.entities.Agent;
import tn.esprit.medicareaitigerback.models.entities.Role;
import tn.esprit.medicareaitigerback.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AgentRepository extends JpaRepository<Agent, Long>, JpaSpecificationExecutor<Agent> {
    Optional<User> findUserByUserName(String userName);

    Optional<Agent> findAgentByUserName(String userName);

    @Query("SELECT a FROM Agent a LEFT JOIN FETCH a.role WHERE a.userName = :userName")
    Optional<Agent> findAgentByUserNameWithRole(@Param("userName") String userName);

    Optional<Agent> findUserByEmail(@Param("email") String email);

    String SQL_AGENT_BY_EMAIL_QUERY = """
            SELECT *
            FROM agent
            INNER JOIN user_entity ON agent.id = user_entity.id
            WHERE agent.id != :id AND email = :email
            """;

    @Query(value = SQL_AGENT_BY_EMAIL_QUERY, nativeQuery = true)
    Agent findAgentByEmailForEdit(@Param("id") Long id, @Param("email") String email);

    String SQL_AGENT_BY_USERNAME_QUERY = """
            SELECT *
            FROM agent
            INNER JOIN user_entity ON agent.id = user_entity.id
            WHERE agent.id != :id AND user_name = :username
            """;

    @Query(value = SQL_AGENT_BY_USERNAME_QUERY, nativeQuery = true)
    Agent findAgentByUsernameForEdit(@Param("id") Long id, @Param("username") String username);

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByRole(Role role);
}
