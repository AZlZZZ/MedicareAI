package tn.esprit.medicareaitigerback.controller.agent;


import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ReasonResponseDTO;
import tn.esprit.medicareaitigerback.services.interfaces.IReasonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/agent/reason")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "110.AGENT.reason", description = "This controller is for reason entity")
public class ReasonAgentController {
    private IReasonService reasonService;
    private static final Logger logger = LoggerFactory.getLogger(ReasonAgentController.class);

    @Operation(
            summary = "Retrieve all reasons",
            description = "",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reasons retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiRespDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid query parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ApiRespDTO.class)
                            )
                    )
            }
    )

    @GetMapping(value = "/getAllReasons")
    public ResponseEntity<ApiRespDTO<List<ReasonResponseDTO>>> getAllReasons(@RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to api/v1/agent/reason/getAllReasons");
            return ResponseEntity.ok(new ApiRespDTO<>(200, "Success", reasonService.getAllReasons(queryValue)));

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API api/v1/agent/reason/getAllReasons executed in {} ms", executionTime);
        }
    }
}
