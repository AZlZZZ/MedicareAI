package tn.esprit.medicareaitigerback.specification;

import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SpecificationsBuilder<T> {


    private final List<SearchCriteria> params = new ArrayList<>();

    public SpecificationsBuilder(List<SearchCriteria> searchCriteriaList) {

        for (SearchCriteria searchCriteria : searchCriteriaList) {
            SearchOperation op = searchCriteria.getOperation();
            if (op != null) {
                if (op == SearchOperation.EQUALITY) {

                    if (searchCriteria.isStartWithAsterisk() && searchCriteria.isEndWithAsterisk()) {
                        op = SearchOperation.CONTAINS;
                    } else if (searchCriteria.isStartWithAsterisk()) {
                        op = SearchOperation.ENDS_WITH;
                    } else if (searchCriteria.isEndWithAsterisk()) {
                        op = SearchOperation.STARTS_WITH;
                    }
                }
                searchCriteria.setOperation(op);
                params.add(searchCriteria);
            }
        }
    }

    public Specification<T> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification result = new MySpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = params.get(i).isOrPredicate()
                    ? Specification.where(result).or(new MySpecification(params.get(i)))
                    : Specification.where(result).and(new MySpecification(params.get(i)));
        }

        return result;
    }

    public Specification<T> buildGrouped() {
        if (params.isEmpty()) return null;

        Map<Integer, List<SearchCriteria>> grouped = params.stream()
                .collect(Collectors.groupingBy(SearchCriteria::getGroup));

        List<SearchCriteria> mainGroup = grouped.getOrDefault(0, List.of());
        Specification<T> spec = mainGroup.stream()
                .map(criteria -> (Specification<T>) new MySpecification<>(criteria))
                .reduce(Specification::and)
                .orElse(null);

        for (Map.Entry<Integer, List<SearchCriteria>> entry : grouped.entrySet()) {
            if (entry.getKey() == 0) continue;

            List<SearchCriteria> groupCriteria = entry.getValue();
            Specification<T> nestedSpec = groupCriteria.stream()
                    .map(criteria -> (Specification<T>) new MySpecification<>(criteria))
                    .reduce(Specification::or)
                    .orElse(null);

            if (spec == null) {
                spec = nestedSpec;
            } else {
                spec = spec.and(nestedSpec);
            }
        }

        return spec;
    }
}