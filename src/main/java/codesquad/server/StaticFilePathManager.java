package codesquad.server;

import java.util.List;

public class StaticFilePathManager {
    private static final List<String> filePaths = List.of("static");

    public List<String> getFilePaths() {
        return filePaths;
    }
}
