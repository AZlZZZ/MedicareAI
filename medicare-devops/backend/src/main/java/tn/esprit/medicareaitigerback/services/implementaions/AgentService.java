package tn.esprit.medicareaitigerback.services.implementaions;
import org.springframework.security.access.AccessDeniedException;
import tn.esprit.medicareaitigerback.config.AppUrlProperties;
import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.business.AuthDTO;
import tn.esprit.medicareaitigerback.models.dto.mapper.AgentMapper;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.request.RegisterAgentDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AgentRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.entities.Agent;
import tn.esprit.medicareaitigerback.models.entities.PasswordResetToken;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.models.enumeration.UserType;
import tn.esprit.medicareaitigerback.repositories.*;
import tn.esprit.medicareaitigerback.security.SecurityUtils;
import tn.esprit.medicareaitigerback.security.UserDetailsImpl;
import tn.esprit.medicareaitigerback.services.interfaces.IAgentService;
import tn.esprit.medicareaitigerback.services.interfaces.IEmailService;
import tn.esprit.medicareaitigerback.specification.SearchCriteria;
import tn.esprit.medicareaitigerback.specification.SpecificationsBuilder;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.management.relation.RoleNotFoundException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
@Service
@AllArgsConstructor
public class AgentService implements IAgentService {

    private AgentRepository agentRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder passwordEncoder;
    private PasswordResetTokenRepository passwordResetTokenRepository;
    private SecurityUtils securityUtils;
    private IEmailService emailService;
    private AppUrlProperties appUrlProperties;
    private AuthenticationManager authenticationManager;
    private UserRepository userRepository;
    private AgentMapper agentMapper;

    @Override
    public void register(RegisterAgentDTO dto) throws RoleNotFoundException, EmailUsedException, PhoneNumberUsedException {
        if (agentRepository.existsByEmail(dto.getEmail())) {
            throw new EmailUsedException("Email is already in use by another agent: " + dto.getEmail());
        }
        if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isEmpty() && agentRepository.existsByPhoneNumber(dto.getPhoneNumber())) {
            throw new PhoneNumberUsedException("Phone number is already in use by another agent: " + dto.getPhoneNumber());
        }
        var agent = agentMapper.fromRegisterAgentDTO(dto);
        agent.setState(UserState.ENABLED);
        // userName = email par défaut pour les agents (utilisé comme identifiant de login)
        agent.setUserName(dto.getEmail());
        agent.setPassword(passwordEncoder.encode(dto.getPassword()));
        var role = roleRepository.findById(dto.getRoleId()).orElseThrow(() -> new RoleNotFoundException("Role not found " + dto.getRoleId()));
        agent.setRole(role);
        agentRepository.save(agent);
    }

    @Override
    public void edit(Long id, RegisterAgentDTO dto) throws EmailUsedException, UserNameUsedException, RoleNotFoundException, AgentNotFoundException {
        var agent = agentRepository.findById(id).orElseThrow(() -> new AgentNotFoundException("Agent Not Found:" + id));
        if (agentRepository.findAgentByEmailForEdit(id, dto.getEmail()) != null) {
            throw new EmailUsedException("Email Used:" + dto.getEmail());
        }
        // Mise à jour des champs sans recréer l'entité (évite la perte de l'id)
        agent.setFirstName(dto.getFirstName());
        agent.setLastName(dto.getLastName());
        agent.setEmail(dto.getEmail());
        agent.setUserName(dto.getEmail()); // userName = email
        agent.setGender(dto.getGender());
        agent.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getBirthDate() != null) {
            try {
                agent.setBirthDate(new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dto.getBirthDate()));
            } catch (java.text.ParseException ignored) {}
        }
        agent.setRole(roleRepository.findById(dto.getRoleId())
                .orElseThrow(() -> new RoleNotFoundException("Role not found: " + dto.getRoleId())));
        // Ne mettre à jour le mot de passe que s'il est fourni
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            agent.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        agentRepository.save(agent);
    }


    @Override
    public AgentRespDTO findAgentById(Long id) throws AgentNotFoundException {
        var agent = agentRepository.findById(id).orElseThrow(() -> new AgentNotFoundException("Agent not found" + id));
        return agentMapper.mapToDTO(agent);
    }

    @Override
    @Transactional
    public Page<AgentRespDTO> findAllAgent(int pageIndex, int numberPerPage, String queryValue, Sort.Direction sortDirection, String sortProperty) {
        Specification<Agent> spec = new SpecificationsBuilder<Agent>(
                Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class))
        ).build();
        Page<Agent> agentPage = agentRepository.findAll(spec, PageRequest.of(pageIndex, numberPerPage, sortDirection, sortProperty));
        return agentPage.map(agentMapper::mapToDTO);

    }

    @Override
    public void requestPasswordReset(String email) throws EmailNotFoundException, MessagingException, UnsupportedEncodingException, AgentNotFoundException, TokenAlreadySentException {
        Agent agent = findByEmail(email);
        if (agent == null) {
            throw new AgentNotFoundException("Agent not found");
        }
        PasswordResetToken resetToken = passwordResetTokenRepository.findByUserId(agent.getId()).orElse(null);
        if (resetToken != null) {
            if (!isExpired(resetToken)) {
                long minutesSinceSend = (System.currentTimeMillis() - (resetToken.getExpirationDate().getTime() - 60 * 60 * 1000)) / (1000 * 60);
                if (minutesSinceSend < 30) {
                    throw new TokenAlreadySentException("A token was already sent less than 30 minutes ago.");
                }
            }
        } else {
            resetToken = new PasswordResetToken();
            resetToken.setUser(agent);
        }
        resetToken.setToken(generateUniqueToken(agent.getId()));
        resetToken.setExpirationDate(new Date(System.currentTimeMillis() + 60 * 60 * 1000)); // +1h
        passwordResetTokenRepository.save(resetToken); // ← sauvegarde du token
        String resetLink = appUrlProperties.getResetPassword() + resetToken.getToken();
        emailService.sendResetEmailAgent(email, resetLink);
    }

    @Override
    public void isValidToken(String token) throws TokenInvalidException {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow((() -> new TokenInvalidException("")));
        if (resetToken.getExpirationDate().before(new Date())) {
            throw new TokenInvalidException("");
        }
    }
    @Override
    public Agent findByEmail(String email) throws EmailNotFoundException {
        return agentRepository.findUserByEmail(email).orElseThrow(() -> new EmailNotFoundException("Email not found: " + email));
    }


    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) throws UnknownUserException, TokenInvalidException, PasswordNotAllowedException, AccessDeniedException {
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
        if (!newPassword.matches(passwordPattern)) {
            throw new PasswordNotAllowedException("Password must be 8–20 characters long and include at least one uppercase, one lowercase, and one number.");
        }
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token).orElseThrow(() -> new TokenInvalidException(""));
        if (resetToken != null) {
            Agent agent = agentRepository.findById(resetToken.getUser().getId()).orElseThrow(() -> new UnknownUserException("User Not Found" + resetToken.getUser().getId()));
            if (resetToken.getExpirationDate().before(new Date())) {
                throw new TokenInvalidException("");
            }
            if (agent != null) {
                agent.setPassword(passwordEncoder.encode(newPassword));
                agentRepository.save(agent);
                deletePasswordResetToken(token);
            }
        }
    }

    @Override
     @Transactional
    public ApiRespDTO<?> signIn(LoginDTO dto) throws UserDontMatchWithApplicationException, UnknownUserException {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(new Gson().toJson(new AuthDTO(dto.getUsername(), UserType.AGENT)), dto.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.badRequest().body(new ApiRespDTO<>(301, "Error username or passowrd", null)).getBody();
        }
        var userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
        if (userPrincipal.getUserType() == UserType.AGENT) {

            Agent refreshedAgent = agentRepository.findAgentByUserName(userPrincipal.getUsername())
                    .orElseThrow(() -> new UnknownUserException("agent not found "));
            SecurityContextHolder.getContext().setAuthentication(authentication);

            Map<String, String> jwt = securityUtils.generateJwtToken2(
                    authentication,
                    UserType.AGENT,
                    null

            );
            String refreshToken = jwt.get("refreshToken");

            refreshedAgent.setRefreshToken(refreshToken);
            agentRepository.save(refreshedAgent);
            return new ApiRespDTO<>(200, "Success", jwt);
        } else {
            throw new UserDontMatchWithApplicationException("user dont match with application");
        }

    }

    @Override
    @Transactional
    public Map<String, String> refreshToken(String accessToken, String refreshToken) throws MismatchBetweenRefreshTokenAndAccessTokenException, TokenNotExpiredYetException, RefreshTokenExpieredException, RefreshTokenInvalideException, UserConnectedFromAnotherDeviceException {
        if (accessToken != null && accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        Claims accessClaims;
        try {
            accessClaims = securityUtils.getClaimsFromJwt(accessToken);
        } catch (ExpiredJwtException e) {
            accessClaims = e.getClaims();
        }
        Claims refreshClaims;
        try {
            refreshClaims = securityUtils.getClaimsFromJwt(refreshToken);
        } catch (ExpiredJwtException e) {
            refreshClaims = e.getClaims();
        }
        Instant accessIat = accessClaims.getIssuedAt().toInstant();
        Instant refreshIat = refreshClaims.getIssuedAt().toInstant();
        if (!accessIat.truncatedTo(ChronoUnit.MINUTES)
                .equals(refreshIat.truncatedTo(ChronoUnit.MINUTES))) {
            throw new MismatchBetweenRefreshTokenAndAccessTokenException("Mismatch Between Refresh Token And Access Token");

        }
        Date expDate = accessClaims.getExpiration();
        if (expDate != null && expDate.after(new Date())) {
            throw new TokenNotExpiredYetException("Access token not expired yet, refresh not allowed");
        }

        Date refrechExpDate = refreshClaims.getExpiration();
        if (refrechExpDate != null && !refrechExpDate.after(new Date())) {
            throw new RefreshTokenExpieredException("refresh token  expired");
        }


        String username = accessClaims.getSubject();

        Agent agent = (Agent) userRepository.findByRefreshToken(refreshToken).orElse(null);
        if (agent == null) {
            String tokenExists = userRepository.findUserByRefreshTokenInAud(refreshToken).orElse(null);
            if (tokenExists == null) {
                throw new RefreshTokenInvalideException("Refresh token invalid");
            } else {
                throw new UserConnectedFromAnotherDeviceException("User connected from another device");
            }
        }

        if (!username.equals(agent.getUserName())) {
            throw new MismatchBetweenRefreshTokenAndAccessTokenException("Mismatch Between Refresh Token And Access Token");
        }

        Authentication authentication = getAuthentication(agent);
        Map<String, String> newTokens = securityUtils.generateJwtToken2(authentication, UserType.AGENT, null);

        agent.setRefreshToken(newTokens.get("refreshToken"));
        userRepository.save(agent);
        return newTokens;
    }

    private Authentication getAuthentication(Agent agent) {
        UserDetailsImpl userDetails = new UserDetailsImpl(
                agent.getId(),
                agent.getUserName(),
                agent.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_AGENT")),
                UserType.AGENT,
                agent.getState()
        );
        return new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
    }
    private boolean isExpired(PasswordResetToken token) {
        return token.getExpirationDate().before(new Date());
    }


    private String generateUniqueToken(long id) {
        String token;
        do {
            String uniquePart = UUID.randomUUID().toString();
            token = "AGE-" + id + "-" + uniquePart;
        } while (passwordResetTokenRepository.existsByToken(token));
        return token;
    }



    @Override
    @Transactional
    public void deletePasswordResetToken(String token) {
        passwordResetTokenRepository.deleteByToken(token);
    }

    @Override
    @Transactional
    public void changeUserState(Long userId, UserState newState, LocalDate unblockDate) throws UnknownUserException {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new UnknownUserException("User not found: " + userId));
        user.setState(newState);
        if (newState == UserState.BLOCKED && unblockDate != null) {
            user.setUnblockDate(unblockDate);
        } else {
            user.setUnblockDate(null);
        }
        userRepository.save(user);
    }

}
