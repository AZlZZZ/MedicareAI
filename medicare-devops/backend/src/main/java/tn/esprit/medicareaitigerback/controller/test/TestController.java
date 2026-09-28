package tn.esprit.medicareaitigerback.controller.test;


import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tn.esprit.medicareaitigerback.models.dto.response.ApiRespDTO;

@RestController
@RequestMapping(value = "api/v1/test")
@AllArgsConstructor
@CrossOrigin
@Tag(name = "400.TEST", description = "This controller is for TEST")
public class TestController {

    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/helloworld")
    public ResponseEntity<ApiRespDTO<Void>> helloworld() {
        return ResponseEntity.ok(new ApiRespDTO<>(200, "Success", null));
    }


}