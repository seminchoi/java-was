package codesquad.http;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ContentType {
    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    TEXT_JAVASCRIPT("js", "text/javascript"),
    TEXT_PLAIN("txt", "text/plain"),

    IMAGE_X_ICON("ico", "image/x-icon"),
    IMAGE_SVG_XML("svg", "image/svg+xml"),
    IMAGE_PNG("png", "image/png"),
    IMAGE_JPG("jpg", "image/jpg"),

    APPLICATION_OCTET_STREAM(".none", "application/octet-stream"),
    APPLICATION_X_WWW_FORM_URLENCODED("xhtml", "application/x-www-form-urlencoded"),

    MULTIPART_FORM_DATA("multipart", "multipart/form-data"),
    ;

    private static final String headerName = "Content-Type";
    private final String fileExtension;
    private final String directive;

    ContentType(String fileExtension, String directive) {
        this.fileExtension = fileExtension;
        this.directive = directive;
    }

    private static final Map<String, ContentType> extensionToContentType = Arrays.stream(ContentType.values())
            .collect(Collectors.toUnmodifiableMap(ContentType::getFileExtension, Function.identity()));

    private static final Map<String, ContentType> directiveToContentType = Arrays.stream(ContentType.values())
            .collect(Collectors.toUnmodifiableMap(ContentType::getDirective, Function.identity()));

    public static ContentType fromFileExtension(String fileExtension) {
        return extensionToContentType.getOrDefault(fileExtension, APPLICATION_OCTET_STREAM);
    }

    public static ContentType fromDirective(String directive) {
        return directiveToContentType.getOrDefault(directive, APPLICATION_OCTET_STREAM);
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getDirective() {
        return directive;
    }

    public String makeHeaderLine() {
        return headerName + ": " + directive + "\r\n";
    }
}
