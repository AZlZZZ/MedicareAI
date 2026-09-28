package tn.esprit.medicareaitigerback.controller.agent;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.request.CreateDoctorRequestDTO;
import tn.esprit.medicareaitigerback.models.dto.request.CreatePatientRequestDTO;
import tn.esprit.medicareaitigerback.models.dto.request.RegisterAgentDTO;
import tn.esprit.medicareaitigerback.models.dto.request.UpdateClientDTO;
import tn.esprit.medicareaitigerback.models.dto.response.*;
import tn.esprit.medicareaitigerback.models.enumeration.UserState;
import tn.esprit.medicareaitigerback.services.interfaces.IAgentService;
import tn.esprit.medicareaitigerback.services.interfaces.IDoctorService;
import tn.esprit.medicareaitigerback.services.interfaces.IPatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping(value = "api/v1/agent/manageUser")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "102.AGENT.manageUser", description = "This controller is for Manage users")
public class ManageUserController {

    private IAgentService agentService;
    private IPatientService clientService;
    private IDoctorService doctorService;
    private static final Logger logger = LoggerFactory.getLogger(ManageUserController.class);
    private static final String SUCCESS = "success";

    @Operation(
            summary = "Register Agent",
            description = """
                    Registers a new agent with the provided details. The agent is assigned a role and enabled by default. 
                    The password is encoded before saving the agent.
                    
                     """
    )

    @PostMapping(value = "/registerAgent")
    public ResponseEntity<ApiRespDTO<Void>> registerAgent(@RequestBody @Valid RegisterAgentDTO dto) throws RoleNotFoundException, EmailUsedException, PhoneNumberUsedException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/registerAgent");
            agentService.register(dto);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/registerAgent executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Edit Agent Information",
            description = "This API allows updating an agent's details such as email, username, role, and other information. " +
                    "It checks for unique constraints on email and username before updating the agent's record."
    )

    @PutMapping(value = "/updateAgent")
    public ResponseEntity<ApiRespDTO<Void>> updateAgent(@RequestParam(name = "id", defaultValue = "0") Long id, @RequestBody @Valid RegisterAgentDTO dto) throws AgentNotFoundException, EmailUsedException, UserNameUsedException, RoleNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/updateAgent");
            agentService.edit(id, dto);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/updateAgent executed in {} ms", executionTime);
        }
    }


    @Operation(
            summary = "Get a paginated list of agents",
            description = """
                    Returns a paginated list of agents based on provided filtering and sorting criteria.
                    
                    **Query value :**
                    
                    **Search by reference:**
                    [{"key":"id","operation":"EQUALITY","value":"value"}]
                    
                    **Search by firstName, lastName, or userName:**
                    [
                        {"key":"firstName","operation":"CONTAINS","value":"value","orPredicate": true},
                        {"key":"lastName","operation":"CONTAINS","value":"value","orPredicate": true},
                        {"key":"userName","operation":"CONTAINS","value":"value","orPredicate": true}
                    ]
                    
                    **Search by userState:**
                    [{"key":"state","operation":"EQUALITY","value":"Value","criteriaType":"USER_STATE"}]
                    """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = SUCCESS, content = @Content(schema = @Schema(implementation = AgentRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Parameters({
            @Parameter(name = "pageIndex", description = "Page index for pagination, starting from 0", required = false, schema = @Schema(defaultValue = "0", type = "integer")),
            @Parameter(name = "numberPerPage", description = "Number of items per page", required = false, schema = @Schema(defaultValue = "10", type = "integer")),
            @Parameter(name = "queryValue", description = "JSON array of search criteria for filtering", required = false, schema = @Schema(defaultValue = "[]", type = "string")),
            @Parameter(name = "sortDirection", description = "Sorting direction, either ASC or DESC", required = false, schema = @Schema(defaultValue = "ASC", type = "string", allowableValues = {"ASC", "DESC"})),
            @Parameter(name = "sortProperty", description = "Property by which to sort the results", required = false, schema = @Schema(defaultValue = "id", type = "string"))
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/agent/findAll")
    public ResponseEntity<ApiRespDTO<Page<AgentRespDTO>>> findAllAgent(@RequestParam(name = "pageIndex", defaultValue = "0", required = false) int pageIndex, @RequestParam(name = "numberPerPage", defaultValue = "10", required = false) int numberPerPage, @RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue, @RequestParam(name = "sortDirection", defaultValue = "ASC", required = false) Sort.Direction sortDirection, @RequestParam(name = "sortProperty", defaultValue = "id", required = false) String sortProperty) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/manageUser/agent/findAll");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, agentService.findAllAgent(pageIndex, numberPerPage, queryValue, sortDirection, sortProperty)));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/agent/findAll executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Find an agent by ID",
            description = """
                    Fetches the details of an agent based on the provided ID.
                  """

    )

   @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/agent/findById")
    public ResponseEntity<ApiRespDTO<AgentRespDTO>> findById(@RequestParam(name = "idAgent", defaultValue = "0") Long idAgent) throws AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/manageUser/agent/findById");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, agentService.findAgentById(idAgent)));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/agent/findById executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Get a paginated list of patients",
            description = ""
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = SUCCESS, content = @Content(schema = @Schema(implementation = ClientRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @Parameters({
            @Parameter(name = "pageIndex", description = "Page index for pagination, starting from 0", required = false, schema = @Schema(defaultValue = "0", type = "integer")),
            @Parameter(name = "numberPerPage", description = "Number of items per page", required = false, schema = @Schema(defaultValue = "10", type = "integer")),
            @Parameter(name = "queryValue", description = "JSON array of search criteria for filtering", required = false, schema = @Schema(defaultValue = "[]", type = "string")),
            @Parameter(name = "sortDirection", description = "Sorting direction, either ASC or DESC", required = false, schema = @Schema(defaultValue = "ASC", type = "string", allowableValues = {"ASC", "DESC"})),
            @Parameter(name = "sortProperty", description = "Property by which to sort the results", required = false, schema = @Schema(defaultValue = "id", type = "string"))
    })

    @GetMapping(value = "/patient/findAll")
    @SecurityRequirement(name = "Bearer Authentication")

    public ResponseEntity<ApiRespDTO<Page<ClientRespDTO>>> findAllPatient(@RequestParam(name = "pageIndex", defaultValue = "0", required = false) int pageIndex, @RequestParam(name = "numberPerPage", defaultValue = "10", required = false) int numberPerPage, @RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue, @RequestParam(name = "sortDirection", defaultValue = "ASC", required = false) Sort.Direction sortDirection, @RequestParam(name = "sortProperty", defaultValue = "id", required = false) String sortProperty) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/manageUser/patient/findAll");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, clientService.findAllClient(pageIndex, numberPerPage, queryValue, sortDirection, sortProperty)));

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/patient/findAll executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Find an patient by ID",
            description = ""

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = SUCCESS,
                    content = @Content(schema = @Schema(implementation = ClientRespDTO.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
           @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping(value = "/patient/findById")
    public ResponseEntity<ApiRespDTO<ClientRespDTO>> findClientById(@RequestParam(name = "idClient", defaultValue = "0") Long idClient) throws PatientNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/manageUser/patient/findById");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, clientService.findById(idClient)));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/patient/findById executed in {} ms", executionTime);
        }
    }



    @Operation(
            summary = "Edit Patient Information"    )
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/updatePatient")
    public ResponseEntity<ApiRespDTO<Void>> updatePatient(@RequestParam(name = "id", defaultValue = "0") Long id,
                                                         @RequestBody @Valid UpdateClientDTO dto) throws EmailUsedException, UserNameUsedException, PhoneNumberUsedException
    {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/updatePatient");
            clientService.edit(id, dto);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/updatePatient executed in {} ms", executionTime);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/createPatient")
    private ResponseEntity<ApiRespDTO<Void>> registerPatient(@RequestBody @Valid CreatePatientRequestDTO dto)
            throws EmailUsedException, AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/createPatient");
            clientService.createPatient(dto);
            return ResponseEntity.ok(new ApiRespDTO<>(200, "success", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/createPatient executed in {} ms", executionTime);
        }
    }
    @Operation(
            summary = " Export List of client ")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "exportPatient")
    public ResponseEntity<Resource> exportClient(@RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue) throws IOException, AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/exportPatient");

            Resource resource = clientService.export(queryValue);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Patients.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/exportPatient executed in {} ms", executionTime);
        }


    }
    @Operation(
            summary = " Export List of agents ")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "exportAgent")
    public ResponseEntity<Resource> exportAgent(@RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue) throws IOException, AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/exportAgent");

            Resource resource = clientService.export(queryValue);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Agents.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/exportAgent executed in {} ms", executionTime);
        }


    }
    @Operation(
            summary = " Export List of doctor ")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "exportDoctor")
    public ResponseEntity<Resource> exportDoctor(@RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue) throws IOException, AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/exportDoctor");

            Resource resource = doctorService.export(queryValue);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Doctors.csv")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);

        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/exportDoctor executed in {} ms", executionTime);
        }


    }
    @Operation(
            summary = "  List of doctor ")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/doctor/findAll")
    public ResponseEntity<ApiRespDTO<Page<DoctorRespDTO>>> findAllDoctor(@RequestParam(name = "pageIndex", defaultValue = "0", required = false) int pageIndex, @RequestParam(name = "numberPerPage", defaultValue = "10", required = false) int numberPerPage, @RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue, @RequestParam(name = "sortDirection", defaultValue = "ASC", required = false) Sort.Direction sortDirection, @RequestParam(name = "sortProperty", defaultValue = "id", required = false) String sortProperty) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/manageUser/doctor/findAll");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, doctorService.findAll(pageIndex, numberPerPage, queryValue, sortDirection, sortProperty)));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/doctor/findAll executed in {} ms", executionTime);
        }
    }
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/doctor/create")
    private ResponseEntity<ApiRespDTO<Void>> registerDoctor(@RequestBody @Valid CreateDoctorRequestDTO dto)
            throws EmailUsedException, AgentNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/manageUser/doctor/create");
            doctorService.create(dto);
            return ResponseEntity.ok(new ApiRespDTO<>(200, "success", null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/manageUser/doctor/create executed in {} ms", executionTime);
        }
    }

    @Operation(summary = "Change user state (ENABLED, BLOCKED, ARCHIVED, DELETED)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/changeUserState")
    public ResponseEntity<ApiRespDTO<Void>> changeUserState(
            @RequestParam(name = "userId") Long userId,
            @RequestParam(name = "state") UserState state,
            @RequestParam(name = "unblockDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate unblockDate
    ) throws UnknownUserException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/manageUser/changeUserState");
            agentService.changeUserState(userId, state, unblockDate);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/agent/manageUser/changeUserState executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }
}
