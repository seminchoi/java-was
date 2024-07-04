package codesquad;

import codesquad.config.StaticFilePathManager;

import java.util.List;

public class TestStaticFilePathManager extends StaticFilePathManager {
    @Override
    public List<String> getFilePaths() {
        return List.of("src/test/resources/static");
    }
}