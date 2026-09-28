package tn.esprit.medicareaitigerback.repositories;

import tn.esprit.medicareaitigerback.models.entities.Patient;
import tn.esprit.medicareaitigerback.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long>, JpaSpecificationExecutor<Patient> {
    Optional<User> findUserByUserName(String userName);

    Optional<Patient> findByEmail(String email);

    Optional<Patient> findClientByUserName(String userName);

    Optional<Patient> findClientByEmail(String email);

    String SQL_CLIENT_BY_EMAIL_QUERY = """
            SELECT *
            FROM patient
            INNER JOIN user_entity ON user_entity.id = patient.id
            WHERE user_entity.email = :email
            """;

    @Query(value = SQL_CLIENT_BY_EMAIL_QUERY, nativeQuery = true)
   Patient getClientByEmail(@Param("email") String email);

    String SQL_CLIENT_BY_USERNAME_QUERY = """
            SELECT *
            FROM patient
            INNER JOIN user_entity ON user_entity.id = patient.id
            WHERE user_entity.user_name = :username
            """;

    @Query(value = SQL_CLIENT_BY_USERNAME_QUERY, nativeQuery = true)
   Patient getClientByUsername(@Param("username") String username);

    String SQL_CLIENT_BY_PHONE_NUMBER_QUERY = """
            SELECT id
            FROM patient
            WHERE phone_number = :phoneNumber
            """;

    @Query(value = SQL_CLIENT_BY_PHONE_NUMBER_QUERY, nativeQuery = true)
    Long getClientByPhoneNumber(@Param("phoneNumber") String phoneNumber);

    List<Patient> findAllByOrderByIdAsc();

}