package tn.esprit.medicareaitigerback.models.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiRespDTO<T> {
    private int code;
    private String msg;
    private T data;
}
