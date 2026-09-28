package tn.esprit.medicareaitigerback.specification;


import tn.esprit.medicareaitigerback.models.enumeration.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.text.ParseException;
import java.text.SimpleDateFormat;

@Data
@NoArgsConstructor
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private String value;
    private SearchCriteriaType criteriaType;
    private String jointEntity;
    private String jointEntity2;
    private String jointAttribut;

    private boolean orPredicate;
    private Integer group = 0;

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
    }

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType, String jointEntity, String jointAttribut) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
        this.jointEntity = jointEntity;
        this.jointAttribut = jointAttribut;
    }

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType, String jointEntity, String jointEntity2, String jointAttribut) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
        this.jointEntity = jointEntity;
        this.jointEntity2 = jointEntity2;
        this.jointAttribut = jointAttribut;
    }

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType, boolean orPredicate) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
        this.orPredicate = orPredicate;
    }

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType, String jointEntity, String jointAttribut, boolean orPredicate) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
        this.jointEntity = jointEntity;
        this.jointAttribut = jointAttribut;
        this.orPredicate = orPredicate;
    }


    public SearchCriteria(String key, SearchOperation operation) {
        this.key = key;
        this.operation = operation;
    }

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType, String jointEntity, String jointEntity2, String jointAttribut, boolean orPredicate) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
        this.jointEntity = jointEntity;
        this.jointEntity2 = jointEntity2;
        this.jointAttribut = jointAttribut;
        this.orPredicate = orPredicate;
    }

    public SearchCriteria(String key, SearchOperation operation, String value, SearchCriteriaType criteriaType, String jointEntity, String jointAttribut, Integer group) {
        this.key = key;
        this.operation = operation;
        this.value = value;
        this.criteriaType = criteriaType;
        this.jointEntity = jointEntity;
        this.jointAttribut = jointAttribut;
        this.group = group;
    }

    public SearchCriteria(String key, SearchOperation searchOperation, String value, SearchCriteriaType searchCriteriaType, int group) {
        this.key = key;
        this.operation = searchOperation;
        this.value = value;
        this.criteriaType = searchCriteriaType;
        this.group = group;
    }

    public boolean isOrPredicate() {
        return orPredicate;
    }

    public Boolean isStartWithAsterisk() {
        return Character.toString(value.charAt(0)).equals("*");
    }

    public Boolean isEndWithAsterisk() {
        return Character.toString(value.charAt(value.length() - 1)).equals("*");
    }


    public Object getTypedValue() {
        if (criteriaType == null) {
            return value;
        }
        String stringValue = value != null ? value : null;
        switch (criteriaType) {
            case BOOLEAN:
                return Boolean.valueOf(value);
            case DATE:
                try {
                    return new SimpleDateFormat("yyyy-MM-dd").parse(value);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            case USER_TYPE:
                return UserType.valueOf(stringValue);
            case GENDER_TYPE:
                return GenderType.valueOf(stringValue);
            case LANGUAGE:
                return Language.valueOf(stringValue);
            case REASON_TYPE:
                return ReasonType.valueOf(stringValue);
            default:
                return value;

        }

    }

    public String getAbsolutValue() {
        switch (operation) {
            case STARTS_WITH:
                return value.substring(0, value.length() - 1);
            case ENDS_WITH:
                return value.substring(1);
            case CONTAINS:
                var str = value.substring(1);
                return str.substring(0, str.length() - 1);
        }
        return value;
    }

}