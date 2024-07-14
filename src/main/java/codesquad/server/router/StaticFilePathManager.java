package codesquad.server.router;

import codesquad.container.Component;

import java.util.List;

@Component
public class StaticFilePathManager {
    private static final List<String> filePaths = List.of("static");

    public List<String> getFilePaths() {
        return filePaths;
    }
}
