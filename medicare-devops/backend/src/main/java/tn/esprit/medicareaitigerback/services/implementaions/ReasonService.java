package tn.esprit.medicareaitigerback.services.implementaions;


import tn.esprit.medicareaitigerback.models.dto.mapper.ReasonMapper;
import tn.esprit.medicareaitigerback.models.dto.response.ReasonResponseDTO;
import tn.esprit.medicareaitigerback.models.entities.Reasons;
import tn.esprit.medicareaitigerback.repositories.ReasonsRepository;
import tn.esprit.medicareaitigerback.services.interfaces.IReasonService;
import tn.esprit.medicareaitigerback.specification.SearchCriteria;
import tn.esprit.medicareaitigerback.specification.SearchCriteriaType;
import tn.esprit.medicareaitigerback.specification.SearchOperation;
import tn.esprit.medicareaitigerback.specification.SpecificationsBuilder;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReasonService implements IReasonService {
    private ReasonsRepository reasonsRepository;
    private ReasonMapper reasonMapper;

    @Override
    public List<ReasonResponseDTO> getAllReasons(String queryValue) {
        var specificationList = new ArrayList<SearchCriteria>();
        specificationList.addAll(Arrays.asList(new Gson().fromJson(queryValue, SearchCriteria[].class)));
        specificationList.add(new SearchCriteria("userType", SearchOperation.EQUALITY, "PATIENT", SearchCriteriaType.USER_TYPE, null, null));
        var specification = new SpecificationsBuilder<Reasons>(specificationList).build();
        List<Reasons> reasons = reasonsRepository.findAll(specification);
        return reasons.stream().map(reasonMapper::mapToDTO).collect(Collectors.toList());
    }
}
