package codesquad.http;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ContentType {
    TEXT_HTML("html"), TEXT_CSS("css"), TEXT_PLAIN("plain");

    private final String fileExtension;

    ContentType(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    private static final Map<String, ContentType> extensionToContentType = Arrays.stream(ContentType.values())
            .collect(Collectors.toUnmodifiableMap(ContentType::getFileExtension, Function.identity()));

    public static ContentType fromFileExtension(String fileExtension) {
        return extensionToContentType.get(fileExtension);
    }

    public String getFileExtension() {
        return fileExtension;
    }
}
