package tn.esprit.medicareaitigerback.controller.patient;

import tn.esprit.medicareaitigerback.exceptions.AddressDontMatchWithUserException;
import tn.esprit.medicareaitigerback.exceptions.AddressNotFoundException;
import tn.esprit.medicareaitigerback.models.dto.request.AddressAddDTO;
import tn.esprit.medicareaitigerback.models.dto.request.AddressUpdDTO;
import tn.esprit.medicareaitigerback.models.dto.response.AddressDTO;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;
import tn.esprit.medicareaitigerback.services.interfaces.IAddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/v1/client/address")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "203.CLIENT.address", description = "This controller is for address entity")
public class AddressPatientController {

    private IAddressService addressService;
    private static final Logger logger = LoggerFactory.getLogger(AddressPatientController.class);
    private static final String SUCCESS = "success";

    @Operation(
            summary = "Add a New Address",
            description = ""

    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "400", description = "Bad Request"),
            @ApiResponse(responseCode = "401", description = "Access Denied"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/add")
    public ResponseEntity<ApiRespDTO<AddressDTO>> add(@RequestBody @Valid AddressAddDTO dto) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/client/address/add");
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, addressService.add(dto)));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/client/address/add executed in {} ms", executionTime);
        }
    }





    @Operation(
            summary = "Update Address",
            description = "",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request: Invalid input data"),

            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping(value = "/update")
    public ResponseEntity<ApiRespDTO<Void>> update(@RequestParam(name = "addressId") Long addressId, @RequestBody @Valid AddressUpdDTO dto) throws  AddressNotFoundException, AddressDontMatchWithUserException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/client/address/update");
            addressService.update(addressId, dto);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/client/address/update executed in {} ms", executionTime);
        }
    }

    @Operation(
            summary = "Delete Address",
            description = "",
            security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Bad request: Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping(value = "/delete")
    public ResponseEntity<ApiRespDTO<Void>> delete(@RequestParam(name = "addressId") Long addressId) throws AddressNotFoundException, AddressDontMatchWithUserException {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        try {
            logger.info("Received API request to /api/v1/client/address/delete");
            addressService.delete(addressId);
            return ResponseEntity.ok(new ApiRespDTO<>(200, SUCCESS, null));
        } finally {
            stopWatch.stop();
            long executionTime = stopWatch.getTotalTimeMillis();
            logger.info("API /api/v1/client/address/delete executed in {} ms", executionTime);
        }
    }
}
