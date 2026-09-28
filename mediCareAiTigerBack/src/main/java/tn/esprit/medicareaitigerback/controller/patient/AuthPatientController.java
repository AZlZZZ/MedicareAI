package tn.esprit.medicareaitigerback.controller.patient;

import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.request.RequestPatientDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientService;

@RestController
@RequestMapping(value = "api/v1/client/auth")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "201.Patient.auth", description = "This controller is for Authentification entity")
public class AuthPatientController {
    private IPatientService clientService;
    private static final Logger logger = LoggerFactory.getLogger(AuthPatientController.class);

  /*  @Operation(
            summary = "Request Password Reset",
            description = """
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
    public ResponseEntity<ApiRespDTO<Void>> requestPasswordReset(@RequestParam(name = "email") String email) throws EmailNotFoundException, UnknownUserException, MessagingException, UnsupportedEncodingException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {

            clientService.requestPasswordReset(email);
            logger.info("Received API request to /api/v1/agent/auth/reset-request");
            return ResponseEntity.ok(new ApiRespDTO<>(200, "Success", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/auth/reset-request executed in {} ms", executionTime);
        }
    }*/

    @Operation(
            summary = "Request Account",
            description = "This API endpoint allows clients to request an account using their phone number/email and password . If the phone number/email is already in use , a `UserNameUsedException` will be thrown . \n"

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account creation successful, returning password for new users."),
            @ApiResponse(responseCode = "201", description = "Account retrieval successful, returning password for existing users."),
            @ApiResponse(responseCode = "301", description = "Error: Invalid credentials or authentication failure.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "3001", description = "email/phone exception",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "1026", description = "DTO Wrong Exception",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class)))
    })
    @PostMapping(value = "/requestAccount")
    public ResponseEntity<ApiRespDTO<Object>> requestAccount(@RequestBody @Valid RequestPatientDTO dto) throws UserNameUsedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/client/auth/requestAccount");
            var result = clientService.requestAccountClient(dto);
            return ResponseEntity.ok(result);
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/client/auth/requestAccount executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Client Sign In",
            description = """
                    This API endpoint is used to authenticate a client using their username and password. If the authentication is successful,
                    it returns a JWT token which can be used for subsequent authenticated requests. Additionally, if mandatory fields in the client's
                    profile are missing or empty, the endpoint returns the client details along with the token for further profile completion.
                    
                    **Workflow:**
                    1. The API receives a login request with the username and password.
                    2. The credentials are authenticated against the authentication manager.
                    3. If the credentials are incorrect, a 400 Bad Request response is returned with an error message.
                    4. If the credentials are correct and the user type is CLIENT:
                       - The API checks if the client's password has expired. If expired, a PasswordExpiredException is thrown.
                       - The authentication is set in the security context.
                       - A JWT token is generated for the authenticated client.
                       - The client's profile is checked for mandatory fields. If any field is missing or empty, the client details along with the token are returned.
                       - If all mandatory fields are filled, only the token is returned.
                    5. If the user type is not CLIENT, a UserDontMatchWithApplicationException is thrown.
                    
                    **Exceptions Handled:**
                    - `UserNotEnabledException`: Thrown when the user is not enabled.
                    - `UserDontMatchWithApplicationException`: Thrown when the user type does not match CLIENT.
                    - `UnknownUserException`: Thrown when the user is not found.
                    - `PasswordExpiredException`: Thrown when the client's password has expired.
                    
                    **Response Codes:**
                    - `200`: Success with JWT token.
                    - `202`: Success with client details and JWT token when mandatory fields are missing or empty.
                    - `301`: Bad Request with error message for incorrect credentials.
                    - `1033`: User not enabled.
                    - `1022`: Password expired.
                    
                    **Response Structure:**
                    - `ApiRespDTO<Object>`: A generic response wrapper with the following fields:
                      - `status`: Status code of the response.
                      - `message`: Message describing the response.
                      - `data`: The response data, which can be a JWT token or a combination of client details and the token.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, returning JWT token"),
            @ApiResponse(responseCode = "202", description = "Authentication successful, returning client details and JWT token for profile completion"),
            @ApiResponse(responseCode = "301", description = "Bad request due to incorrect username or password"),
            @ApiResponse(responseCode = "1033", description = "User not enabled",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "1022", description = " password expired ",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),

    })
    @PostMapping(value = "/signIn")
    public ResponseEntity<ApiRespDTO<Object>> signIn(@RequestBody LoginDTO dto) throws UserDontMatchWithApplicationException, UnknownUserException, PasswordExpiredException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/client/auth/signIn");
            var result = clientService.signIn(dto);
            return ResponseEntity.ok(result);

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/client/auth/signIn  executed in {} ms", executionTime);
        }
    }

  /*  @Operation(
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
                    - `TokenNotFoundException`: Thrown when the provided reset token is invalid or expired.
                    - `UnknownUserException`: Thrown when the user associated with the token is not found.
                    
                    **Response Codes:**
                    - `200 OK`: Password reset successfully.
                    - `400 Bad Request`: Invalid or expired token.
                    - `500 Internal Server Error`: An unexpected error occurred.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password updated successfully.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "3007", description = "token expired .",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "3028", description = "Invalid  token.",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error.",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/reset")
    public ResponseEntity<ApiRespDTO<Void>> resetPassword(@RequestParam(name = "token") String token, @RequestParam(name = "newPassword") String newPassword) throws TokenNotFoundException, UnknownUserException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/client/auth/reset");
            clientService.PasswordResetByToken(token, newPassword);
            return ResponseEntity.ok(new ApiRespDTO<>(200, "Password updated successfully.", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/client/auth/reset executed in {} ms", executionTime);
        }
    }*/


}
