package codesquad.server.router;

import codesquad.container.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StaticFilePathManager {
    private static final List<String> filePaths = new ArrayList<>();

    public List<String> getFilePaths() {
        return filePaths;
    }

    public StaticFilePathManager addPath(String path) {
        filePaths.add(path);
        return this;
    }
}
