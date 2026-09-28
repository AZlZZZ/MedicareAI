package tn.esprit.medicareaitigerback.specification;

public enum SearchOperation {
    EQUALITY, NEGATION, IS_NULL, IS_NOT_NULL, GREATER_THAN, LESS_THAN, LIKE, STARTS_WITH, ENDS_WITH, NOT_CONTAINS, CONTAINS, GROUP_JOIN_EQUALITY, GROUP_JOIN_CONTAINS, GROUP_JOIN_NOT_CONTAINS, GROUP_JOIN_CONTAINS2, GROUP_JOIN_IS_NOT_NULL, GROUP_JOIN_IS_NULL, GROUP_JOIN_NEGATION, GROUP_JOIN_EQUALITY2;

    public static final String[] SIMPLE_OPERATION_SET = {":", "!", ">", "<", "~"};

    public static SearchOperation getSimpleOperation(char input) {
        switch (input) {
            case ':':
                return EQUALITY;
            case '!':
                return NEGATION;
            case '>':
                return GREATER_THAN;
            case '<':
                return LESS_THAN;
            case '~':
                return LIKE;
            default:
                return null;
        }
    }
}