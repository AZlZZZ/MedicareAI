package tn.esprit.medicareaitigerback.services.interfaces;


import tn.esprit.medicareaitigerback.models.dto.response.ReasonResponseDTO;

import java.util.List;

public interface IReasonService {

    List<ReasonResponseDTO> getAllReasons(String queryValue) ;
}
