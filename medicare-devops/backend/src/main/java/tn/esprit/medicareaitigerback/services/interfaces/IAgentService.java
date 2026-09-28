package tn.esprit.medicareaitigerback.services.interfaces;

import jakarta.mail.MessagingException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.request.RegisterAgentDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AgentRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import tn.esprit.medicareaitigerback.models.entities.Agent;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;

import javax.management.relation.RoleNotFoundException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.Map;

public interface IAgentService {
    void register(RegisterAgentDTO dto) throws RoleNotFoundException, EmailUsedException, PhoneNumberUsedException;

    @Secured({"ROLE_AGENT"})
    void edit(Long id, RegisterAgentDTO dto) throws AgentNotFoundException, EmailUsedException, UserNameUsedException, RoleNotFoundException;

    @Secured({"ROLE_AGENT"})
    AgentRespDTO findAgentById(Long id) throws AgentNotFoundException;

    @Secured({"ROLE_AGENT"})
    Page<AgentRespDTO> findAllAgent(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty);

    void requestPasswordReset(String email) throws EmailNotFoundException, MessagingException, UnsupportedEncodingException, AgentNotFoundException, TokenAlreadySentException;

    void isValidToken(String token) throws TokenInvalidException;

    void resetPassword(String token, String newPassword) throws UnknownUserException, TokenInvalidException, PasswordNotAllowedException, AccessDeniedException;

    ApiRespDTO<?> signIn(LoginDTO dto) throws UserDontMatchWithApplicationException, UnknownUserException;

    Map<String, String> refreshToken(String accessToken, String refreshToken) throws MismatchBetweenRefreshTokenAndAccessTokenException, TokenNotExpiredYetException, RefreshTokenExpieredException, RefreshTokenInvalideException, UserConnectedFromAnotherDeviceException;

    Agent findByEmail(String email) throws EmailNotFoundException;

    void deletePasswordResetToken(String token);

    @Secured({"ROLE_AGENT"})
    void changeUserState(Long userId, UserState newState, LocalDate unblockDate) throws UnknownUserException;
}
