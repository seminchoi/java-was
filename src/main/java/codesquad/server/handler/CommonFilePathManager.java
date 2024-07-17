package codesquad.server.handler;

import codesquad.container.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CommonFilePathManager {
    private static final List<String> filePaths = new ArrayList<>();

    public List<String> getFilePaths() {
        return filePaths;
    }

    public CommonFilePathManager addPath(String path) {
        filePaths.add(path);
        return this;
    }
}
