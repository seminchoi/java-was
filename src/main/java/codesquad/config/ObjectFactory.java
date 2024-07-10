package codesquad.config;

import codesquad.storage.UserStorage;
import codesquad.server.RequestHandler;
import codesquad.server.RouteEntryManager;
import codesquad.server.StaticFilePathManager;
import codesquad.server.StaticFileProcessor;
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

    protected StaticFileProcessor resourcePathManager() {
        return new StaticFileProcessor(staticFilePathManager());
    }

    protected StaticFilePathManager staticFilePathManager() {
        return new StaticFilePathManager();
    }
}
