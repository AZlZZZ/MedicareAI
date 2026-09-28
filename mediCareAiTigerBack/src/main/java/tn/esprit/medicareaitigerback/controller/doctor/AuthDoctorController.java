package tn.esprit.medicareaitigerback.controller.doctor;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;
import tn.esprit.medicareaitigerback.exceptions.UnknownUserException;
import tn.esprit.medicareaitigerback.exceptions.UserDontMatchWithApplicationException;
import tn.esprit.medicareaitigerback.models.dto.request.LoginDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.services.interfaces.IDoctorService;

@RestController
@RequestMapping(value = "api/v1/doctor/auth")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "201.Doctor.auth", description = "This controller is for Doctor Authentication")
public class AuthDoctorController {

    private IDoctorService doctorService;
    private static final Logger logger = LoggerFactory.getLogger(AuthDoctorController.class);

    @Operation(
            summary = "Doctor Sign In",
            description = "Authenticates a doctor using username and password. Returns a JWT token on success."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful, returning JWT token"),
            @ApiResponse(responseCode = "301", description = "Bad request due to incorrect username or password"),
            @ApiResponse(responseCode = "1011", description = "User does not match the expected application type",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ApiRespDTO.class)))
    })
    @PostMapping(value = "/signIn")
    public ResponseEntity<ApiRespDTO<Object>> signIn(@RequestBody LoginDTO dto)
            throws UserDontMatchWithApplicationException, UnknownUserException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/doctor/auth/signIn");
            var result = doctorService.signIn(dto);
            return ResponseEntity.ok(result);
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/doctor/auth/signIn executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }
}
