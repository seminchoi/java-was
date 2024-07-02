package codesquad;

import codesquad.config.ResourcePathManager;

import java.util.List;

public class TestResourcePathManager extends ResourcePathManager {
    @Override
    public List<String> getFilePaths() {
        return List.of("src/test/resources");
    }
}