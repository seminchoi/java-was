package codesquad;

import codesquad.server.StaticFilePathManager;

import java.util.List;

public class TestStaticFilePathManager extends StaticFilePathManager {
    @Override
    public List<String> getFilePaths() {
        return List.of("/static");
    }
}