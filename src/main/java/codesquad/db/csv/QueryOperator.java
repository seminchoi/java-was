package codesquad.db.csv;

import java.util.function.BiPredicate;

public enum QueryOperator {
    EQUAL('=', (a, b) -> a.compareTo(b) == 0),
    GREATER('>', (a, b) -> a.compareTo(b) > 0),
    LESS('<', (a, b) -> a.compareTo(b) < 0),
    ;

    private final char sign;
    private final BiPredicate<String, String> operator;

    QueryOperator(char sign, BiPredicate<String, String> operator) {
        this.sign = sign;
        this.operator = operator;
    }

    public char getSign() {
        return sign;
    }

    public BiPredicate<String, String> getOperator() {
        return operator;
    }

    public static QueryOperator fromSign(char sign) {
        for (QueryOperator op : values()) {
            if (op.getSign() == sign) {
                return op;
            }
        }
        throw new IllegalArgumentException("No matching QueryOperator for sign: " + sign);
    }
}
