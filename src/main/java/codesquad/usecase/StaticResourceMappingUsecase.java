package codesquad.usecase;

import codesquad.config.StaticFileProcessor;
import codesquad.http.HttpResponse;

public class StaticResourceMappingUsecase {
    private final StaticFileProcessor staticFileProcessor;

    public StaticResourceMappingUsecase(StaticFileProcessor staticFileProcessor) {
        this.staticFileProcessor = staticFileProcessor;
    }

    public HttpResponse register() {
        return staticFileProcessor.readFile("/registration/index.html");
    }
}
