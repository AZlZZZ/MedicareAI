package tn.esprit.medicareaitigerback.controller.agent;

import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AgentDetails;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ClientRespDTO;
import tn.esprit.medicareaitigerback.repositories.AgentRepository;
import tn.esprit.medicareaitigerback.repositories.UserRepository;
import tn.esprit.medicareaitigerback.security.SecurityUtils;
import tn.esprit.medicareaitigerback.services.interfaces.IAgentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@RestController
@RequestMapping(value = "api/v1/agent/auth")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "101.AGENT.auth", description = "This controller is for Authentification entity")
public class AuthAgentController {
    private AuthenticationManager authenticationManager;
    private SecurityUtils securityUtils;
    private BCryptPasswordEncoder passwordEncoder;
    private AgentRepository agentRepository;
    private IAgentService agentService;
    private UserRepository userRepository;
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final Logger logger = LoggerFactory.getLogger(AuthAgentController.class);

    @Operation(
            summary = "Request Password Reset",
            description =
                    """
                            Sends a password reset link to the user's email if the email is registered in the system.
                            
                            **Workflow:**
                            1. The API receives the user's email address as a request parameter.
                            2. The system checks if the email exists in the database.
                            3. If the email is found, a password reset token is generated and sent to the user's email.
                            4. If the email is not found, an `EmailNotFoundException` is thrown.
                            
                            **Parameters:**
                            - `email` (String): The email address associated with the user's account.
                            
                            **Exceptions Handled:**
                            - `EmailNotFoundException`: Thrown when the provided email is not found in the system.
                            
                            **Response Codes:**
                            - `200 OK`: Reset link sent successfully.
                            - `1002 Not Found`: The email was not found in the system.
                            - `500 Internal Server Error`: An unexpected error occurred.
                            """

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reset email sent successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "1001", description = "Agent not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "1002", description = "email not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/reset-request")
    public ResponseEntity<ApiRespDTO<Void>> requestPasswordReset(@RequestParam(name = "email") String email) throws EmailNotFoundException, MessagingException, UnsupportedEncodingException, TokenAlreadySentException, AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            agentService.requestPasswordReset(email);
            logger.info("Received API request to /api/v1/agent/auth/reset-request");
            return ResponseEntity.ok(new ApiRespDTO<>(200, "A password reset email has been sent successfully", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/auth/reset-request executed in {} ms", executionTime);
        }
    }


    @Operation(
            summary = "Sign In",
            description = """
                    Authenticates the user based on the provided username and password.
                    If authentication is successful, a JWT token is generated and returned. 
                    If the credentials are incorrect or the user does not match the application type, an appropriate exception is thrown.
                    
                    **Workflow:**
                    1. The API receives the login credentials (username and password) in the request body.
                    2. The API attempts to authenticate the user using the provided credentials.
                    3. If authentication fails due to incorrect credentials, a `BadCredentialsException` is thrown and a 400 Bad Request response is returned.
                    4. If authentication succeeds and the user type matches `AGENT`, a JWT token is generated and returned in the response.
                    5. If the user type does not match, a `UserDontMatchWithApplicationException` is thrown.
                    
                    **Parameters:**
                    - `dto` (LoginDTO): The DTO containing the username and password for authentication.
                    
                    **Exceptions Handled:**
                    - `UserDontMatchWithApplicationException`: Thrown when the authenticated user does not match the expected application type.
                    - `BadCredentialsException`: Thrown when the provided username or password is incorrect.
                    
                    **Response Codes:**
                    - `200 OK`: Authentication successful; a JWT token is returned in the response body.
                    - `1011 Bad Request`: User does not match the expected application type.
                     - `500 Internal Server Error`: An unexpected error occurred during the processing of the request.
                    """
    )

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful; JWT token returned",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "1011", description = "user does not match the expected application type",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping(value = "/signIn")
    public ResponseEntity<Object> signIn(@RequestBody LoginDTO dto) throws UserDontMatchWithApplicationException, UnknownUserException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/auth/signIn — username={}", dto.getUsername());
            var result = agentService.signIn(dto);
            // ⚡ Récupère accessToken du résultat
            Map<String, String> tokens = (Map<String, String>) result.getData();
            String accessToken = tokens.get(ACCESS_TOKEN_KEY);
            String refreshToken = tokens.get(REFRESH_TOKEN_KEY);

            // ⚡ Création cookie HttpOnly + Secure
            ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN_KEY, accessToken)
                    .httpOnly(true)              // interdit accès JS
                    .secure(true)                // true en prod avec HTTPS
                    .sameSite("Strict")          // ou "Lax" selon ton besoin
                    .path("/")                   // valable sur tout le site
                    .maxAge(60 * 60 * 24)             // 1Jour  (exemple)
                    .build();

            // ⚡ Tu peux aussi mettre un cookie pour refreshToken si tu veux
            ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_KEY, refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(60L * 60 * 24 * 7)   // 7 jours
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                    .body(result);
            //.body(new ApiRespDTO<>(200, "Login success", null));
            //return ResponseEntity.ok(result);
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/auth/signIn executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Reset Password",
            description = """
                    Allows users to reset their password using a valid password reset token.
                    
                    **Workflow:**
                    1. The API receives the password reset token and new password as request parameters.
                    2. The system validates the token.
                    3. If the token is valid, the user's password is updated and the token is deleted.
                    4. If the token is invalid or expired, an `InvalidTokenException` is thrown.
                    
                    **Parameters:**
                    - `token` (String): The password reset token sent to the user's email.
                    - `newPassword` (String): The new password to set for the user.
                    
                    **Exceptions Handled:**
                    - `TokenInvalidException`: Thrown when the provided reset token is invalid or expired.
                    - `PasswordNotAllowedException`: Password not allowed (does not meet the rules).
                    - `UnknownUserException`: Thrown when the user associated with the token is not found.
                    
                    **Response Codes:**
                    - `200 OK`: Password reset successfully.
                    - `3082 TokenInvalidException`: Invalid or expired token.
                    - `1152 PasswordNotAllowedException`: Password not allowed (does not meet the rules).
                    - `1010 UnknownUserException`: User associated with the token is not found.
                    - `500 Internal Server Error`: An unexpected error occurred.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "1001", description = "Agent Not Found "),
            @ApiResponse(responseCode = "1164", description = "token  Invalid  token.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "1152", description = "Password not allowed (does not meet the rules)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "1010", description = "User associated with the token is not found.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiRespDTO<Void>> resetPassword(@RequestParam(name = "token") String token, @RequestParam(name = "newPassword") String newPassword) throws UnknownUserException, PasswordNotAllowedException, TokenInvalidException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/auth/reset");
            agentService.resetPassword(token, newPassword);
            return ResponseEntity.ok(new ApiRespDTO<>(200, "Password updated successfully.", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/auth/reset executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Validate Password Reset Token",
            description = """
                    Validates whether a password reset token is still valid (not expired and exists in the system).
                    
                    **Workflow:**
                    1. The API receives the password reset token as a request parameter.
                    2. The system searches for the token in the database.
                    3. If the token exists and is not expired, the validation passes.
                    4. If the token does not exist or expired, a `TokenInvalidException` is thrown.
                    
                    **Parameters:**
                    - `token` (String): The password reset token sent to the user's email.
                    
                    **Exceptions Handled:**
                    - `TokenInvalidException`: Thrown when the provided token does not exist or invalid.
                    
                    **Response Codes:**
                    - `200 OK`: Token is valid.
                    - `3082 TokenInvalidException`: Token expired.
                    - `500 Internal Server Error`: An unexpected error occurred.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "1083", description = "Token invalid .",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/validatePasswordToken")
    public ResponseEntity<ApiRespDTO<Void>> validateToken(
            @RequestParam(name = "token") String token) throws TokenInvalidException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/auth/validatePasswordToken");

            agentService.isValidToken(token);

            return ResponseEntity.ok(new ApiRespDTO<>(200, "Token is valid.", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/auth/validatePasswordToken executed in {} ms", executionTime);
        }
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<ApiRespDTO<Map<String, String>>> refreshToken(
            String accessToken,
            @RequestParam String refreshToken
    ) throws TokenNotExpiredYetException, MismatchBetweenRefreshTokenAndAccessTokenException, RefreshTokenExpieredException, RefreshTokenInvalideException, UserConnectedFromAnotherDeviceException {

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/auth/refreshToken");
            return ResponseEntity.ok(new ApiRespDTO<>(200, "Token refreshed successfully", agentService.refreshToken(accessToken, refreshToken)));

        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/agent/auth/refreshToken executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiRespDTO<Object>> logout(HttpServletResponse response) {
        ResponseCookie accessCookie = ResponseCookie.from(ACCESS_TOKEN_KEY, "")
                .httpOnly(true)
                .secure(true)  // true en prod HTTPS
                .path("/")
                .maxAge(0)
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from(REFRESH_TOKEN_KEY, "")
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new ApiRespDTO<>(200, "Logout successful", null));
    }

}

