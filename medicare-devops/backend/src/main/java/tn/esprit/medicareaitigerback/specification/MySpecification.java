package tn.esprit.medicareaitigerback.specification;


import jakarta.persistence.criteria.*;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


@AllArgsConstructor
public class MySpecification<D> implements Specification<D> {

    private SearchCriteria criteria;

    @Override
    public Predicate toPredicate(
            Root<D> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        switch (criteria.getOperation()) {
            case EQUALITY:
                return builder.equal(root.get(criteria.getKey()), criteria.getTypedValue());
            case NEGATION:
                return builder.notEqual(root.get(criteria.getKey()), criteria.getTypedValue());
            case IS_NULL:
                return builder.isNull(root.get(criteria.getKey()));
            case IS_NOT_NULL:
                return builder.isNotNull(root.get(criteria.getKey()));
            case GREATER_THAN:
                if (criteria.getTypedValue() instanceof Date) {
                    return builder.greaterThan(root.get(criteria.getKey()), (Date) criteria.getTypedValue());
                } else {
                    String dateString = criteria.getTypedValue().toString();

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = dateFormat.parse(dateString);
                        return builder.greaterThan(root.get(criteria.getKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

            case LESS_THAN:
                if (criteria.getTypedValue() instanceof Date) {
                    return builder.lessThan(root.get(criteria.getKey()), (Date) criteria.getTypedValue());
                } else {
                    String dateString = criteria.getTypedValue().toString();

                    try {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = dateFormat.parse(dateString);
                        return builder.lessThan(root.get(criteria.getKey()), date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            case LIKE:
                return builder.like(root.get(criteria.getKey()), criteria.getTypedValue().toString());
            case STARTS_WITH:
                return builder.like(root.get(criteria.getKey()), criteria.getAbsolutValue() + "%");
            case ENDS_WITH:
                return builder.like(root.get(criteria.getKey()), "%" + criteria.getAbsolutValue());
            case CONTAINS:
                return builder.like(root.get(
                        criteria.getKey()), "%" + criteria.getAbsolutValue() + "%");
            case NOT_CONTAINS:
                return builder.notLike(root.get(
                        criteria.getKey()), "%" + criteria.getAbsolutValue() + "%");
            case GROUP_JOIN_EQUALITY:
                Join groupJoinEquality = root.join(criteria.getJointEntity());
                return builder.equal(groupJoinEquality.get(criteria.getJointAttribut()), criteria.getTypedValue());
            case GROUP_JOIN_CONTAINS:
                Join groupJoinContains = root.join(criteria.getJointEntity());
                return builder.like(groupJoinContains.get(criteria.getJointAttribut()), "%" + criteria.getTypedValue() + "%");
            case GROUP_JOIN_NOT_CONTAINS:
                Join groupJoinNotContains = root.join(criteria.getJointEntity());
                return builder.notLike(groupJoinNotContains.get(criteria.getJointAttribut()), "%" + criteria.getTypedValue() + "%");
            case GROUP_JOIN_CONTAINS2:
                Join groupJoinContains2 = root.join(criteria.getJointEntity()).join(criteria.getJointEntity2());
                return builder.like(groupJoinContains2.get(criteria.getJointAttribut()), "%" + criteria.getTypedValue() + "%");
            case GROUP_JOIN_IS_NOT_NULL:
                Join groupJoinNotNull = root.join(criteria.getJointEntity());
                return builder.isNotNull(groupJoinNotNull.get(criteria.getJointAttribut()));
            case GROUP_JOIN_IS_NULL:
                Join groupJoinNull = root.join(criteria.getJointEntity());
                return builder.isNull(groupJoinNull.get(criteria.getJointAttribut()));
            case GROUP_JOIN_NEGATION:
                Join groupJoinNegation = root.join(criteria.getJointEntity());
                return builder.notEqual(groupJoinNegation.get(criteria.getJointAttribut()), criteria.getTypedValue());
            case GROUP_JOIN_EQUALITY2:
                Join groupJoinEquality2 = root.join(criteria.getJointEntity()).join(criteria.getJointEntity2());
                return builder.equal(groupJoinEquality2.get(criteria.getJointAttribut()), criteria.getTypedValue());

            default:
                return null;
        }
    }
}
