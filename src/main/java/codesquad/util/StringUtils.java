package codesquad.util;

public class StringUtils {
    private static final String CRLF = "\r\n";
    public static String addCrlf(String target) {
        return target + CRLF;
    }

    public static String crlf() {
        return CRLF;
    }
}
