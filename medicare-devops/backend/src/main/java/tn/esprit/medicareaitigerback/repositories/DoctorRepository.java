package tn.esprit.medicareaitigerback.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.esprit.medicareaitigerback.models.entities.Doctor;
import tn.esprit.medicareaitigerback.models.entities.Patient;
import tn.esprit.medicareaitigerback.models.entities.User;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long>, JpaSpecificationExecutor<Doctor> {
    Optional<User> findUserByUserName(String userName);

    Optional<Doctor> findByEmail(String email);

    Optional<Doctor> findByUserName(String userName);


    String SQL_DOCTOR_BY_EMAIL_QUERY = """
            SELECT *
            FROM doctor
            INNER JOIN user_entity ON user_entity.id = doctor.id
            WHERE user_entity.email = :email
            """;

    @Query(value = SQL_DOCTOR_BY_EMAIL_QUERY, nativeQuery = true)
   Patient getDoctorByEmail(@Param("email") String email);

    String SQL_DOCTOR_BY_USERNAME_QUERY = """
            SELECT *
            FROM doctor
            INNER JOIN user_entity ON user_entity.id = doctor.id
            WHERE user_entity.user_name = :username
            """;

    @Query(value = SQL_DOCTOR_BY_USERNAME_QUERY, nativeQuery = true)
   Patient getDoctorByUsername(@Param("username") String username);

    String SQL_DOCTOR_BY_PHONE_NUMBER_QUERY = """
            SELECT id
            FROM doctor
            WHERE phone_number = :phoneNumber
            """;

    @Query(value = SQL_DOCTOR_BY_PHONE_NUMBER_QUERY, nativeQuery = true)
    Long getDoctorByPhoneNumber(@Param("phoneNumber") String phoneNumber);


}