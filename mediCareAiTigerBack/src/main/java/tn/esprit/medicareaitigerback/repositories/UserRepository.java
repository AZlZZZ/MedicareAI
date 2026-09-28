package tn.esprit.medicareaitigerback.repositories;

import tn.esprit.medicareaitigerback.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUserName(String userName);

    Optional<User> findUserByEmail(@Param("email") String email);

    Optional<User> findByRefreshToken(String refreshToken);

    @Query(value = """
            SELECT refresh_token
            FROM user_entity_aud
            WHERE refresh_token = :refreshToken
            ORDER BY rev DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<String> findUserByRefreshTokenInAud(@Param("refreshToken") String refreshToken);
}
