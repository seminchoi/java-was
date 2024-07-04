package codesquad.config;

import codesquad.domain.UserStorage;
import codesquad.http.RequestHandler;
import codesquad.usecase.StaticResourceMappingUsecase;
import codesquad.usecase.UserUsecase;

public class ObjectFactory {
    protected UserUsecase userUsecase() {
        return new UserUsecase(userStorage());
    }

    protected UserStorage userStorage() {
        return new UserStorage();
    }

    protected RequestHandler requestHandler() {
        return new RequestHandler(routeEntryManager(), resourcePathManager());
    }

    protected RouteEntryManager routeEntryManager() {
        return new RouteEntryManager();
    }

    protected StaticResourceMappingUsecase requestMappingUsecase() {
        return new StaticResourceMappingUsecase(resourcePathManager());
    }

    protected StaticFileProcessor resourcePathManager() {
        return new StaticFileProcessor(staticFilePathManager());
    }

    protected StaticFilePathManager staticFilePathManager() {
        return new StaticFilePathManager();
    }
}
