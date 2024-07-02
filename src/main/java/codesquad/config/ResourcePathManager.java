package codesquad.config;

import java.util.List;

public class ResourcePathManager {
    private static final List<String> filePaths = List.of("src/main/resources/static");

    public List<String> getFilePaths() {
        return filePaths;
    }
}
