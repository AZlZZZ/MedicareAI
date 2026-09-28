package tn.esprit.medicareaitigerback.models.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateMedicamentDTO {

    @NotBlank
    @Size(max = 120)
    private String name;

    private String description;

    @Min(0)
    private Integer stock = 0;
}

