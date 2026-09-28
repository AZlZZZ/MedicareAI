package tn.esprit.medicareaitigerback.controller.agent;

import tn.esprit.medicareaitigerback.exceptions.*;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.models.dto.response.RoleDTO;
import tn.esprit.medicareaitigerback.models.dto.response.RoleResponseDTO;
import tn.esprit.medicareaitigerback.services.interfaces.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/v1/agent/role")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "104.AGENT.role", description = "This controller is for Role and Function entities")
public class RoleController {
    private IRoleService roleService;
    private static final Logger logger = LoggerFactory.getLogger(RoleController.class);
    private static final String SUCCESS = "success";


    @Operation(
            summary = "Retrieve a list of all roles",
            description = """
                    Returns a list of all available roles in the system, including details such as role Id and role name.
                    
                    **Response Codes:**
                    - `200 OK`: Roles retrieved successfully.
                    - `500 Internal Server Error`: Unexpected error during processing.
                    """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success - Roles retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RoleDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @Parameters({
            @Parameter(name = "pageIndex", description = "Page index for pagination, starting from 0", required = false, schema = @Schema(defaultValue = "0", type = "integer")),
            @Parameter(name = "numberPerPage", description = "Number of items per page", required = false, schema = @Schema(defaultValue = "10", type = "integer")),
            @Parameter(name = "queryValue", description = "JSON array of search criteria for filtering", required = false, schema = @Schema(defaultValue = "[]", type = "string")),
            @Parameter(name = "sortDirection", description = "Sorting direction, either ASC or DESC", required = false, schema = @Schema(defaultValue = "ASC", type = "string", allowableValues = {"ASC", "DESC"})),
            @Parameter(name = "sortProperty", description = "Property by which to sort the results", required = false, schema = @Schema(defaultValue = "id", type = "string"))
    })
//    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/findAll")
    public ResponseEntity<ApiRespDTO<Page<RoleResponseDTO>>> getListRole(@RequestParam(name = "pageIndex", defaultValue = "0", required = false) int pageIndex, @RequestParam(name = "numberPerPage", defaultValue = "10", required = false) int numberPerPage, @RequestParam(name = "queryValue", defaultValue = "[]", required = false) String queryValue, @RequestParam(name = "sortDirection", defaultValue = "ASC", required = false) Sort.Direction sortDirection, @RequestParam(name = "sortProperty", defaultValue = "id", required = false) String sortProperty) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/role/findAll");

            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, roleService.getAllRole(pageIndex, numberPerPage, queryValue, sortDirection, sortProperty)));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/role/findAll executed in {} ms", executionTime);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/create")
    public ResponseEntity<ApiRespDTO<Void>> createRole(@RequestParam(name = "name") String name) throws RoleAlreadyExistException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            logger.info("Received API request to /api/v1/agent/role/create");
            roleService.createRole(name);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            logger.info("API /api/v1/agent/role/create executed in {} ms", stopWatch.getTotalTimeMillis());
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/rename")
    public ResponseEntity<ApiRespDTO<Void>> renameRole(@RequestParam(name = "roleId", defaultValue = "0") Long roleId, @RequestParam(name = "name") String name) throws RoleNotFoundException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/role/rename");
            roleService.updateRoleName(roleId, name);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/role/rename executed in {} ms", executionTime);
        }
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<ApiRespDTO<Void>> renameRole(@RequestParam(name = "roleId", defaultValue = "0") Long roleId) throws RoleNotFoundException, RoleInUseException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/agent/role/delete");
            roleService.deleteRole(roleId);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/agent/role/delete executed in {} ms", executionTime);
        }
    }
}
