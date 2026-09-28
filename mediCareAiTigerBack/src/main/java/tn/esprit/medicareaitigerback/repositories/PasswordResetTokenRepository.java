package tn.esprit.medicareaitigerback.repositories;

import tn.esprit.medicareaitigerback.models.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);

    String SQL_PASSWORD_RESET_TOKEN_BY_USER_ID = """
            SELECT p
            FROM PasswordResetToken p
            WHERE p.user.id = :id
            """;

    @Query(SQL_PASSWORD_RESET_TOKEN_BY_USER_ID)
    Optional<PasswordResetToken> findByUserId(Long id);

    Boolean existsByToken(String token);

    void deleteByToken(String token);
}
