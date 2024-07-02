package codesquad.http;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ContentType {
    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    TEXT_PLAIN("txt", "text/plain"),
    APPLICATION_OCTET_STREAM(".none", "application/octet-stream");

    private static final String headerName = "Content-Type";
    private final String fileExtension;
    private final String directive;

    ContentType(String fileExtension, String directive) {
        this.fileExtension = fileExtension;
        this.directive = directive;
    }

    private static final Map<String, ContentType> extensionToContentType = Arrays.stream(ContentType.values())
            .collect(Collectors.toUnmodifiableMap(ContentType::getFileExtension, Function.identity()));

    public static ContentType fromFileExtension(String fileExtension) {
        return extensionToContentType.getOrDefault(fileExtension, APPLICATION_OCTET_STREAM);
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String makeHeaderLine() {
        return headerName + ": " + directive + "\r\n";
    }
}
