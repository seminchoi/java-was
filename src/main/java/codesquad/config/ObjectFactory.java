package codesquad.config;

import codesquad.security.SessionStorage;
import codesquad.storage.UserStorage;
import codesquad.server.router.RequestHandler;
import codesquad.server.router.RouteEntryManager;
import codesquad.server.router.StaticFilePathManager;
import codesquad.server.router.StaticFileProcessor;
import codesquad.usecase.UserUsecase;

public class ObjectFactory {
    protected UserUsecase userUsecase() {
        return new UserUsecase(userStorage(), sessionStorage());
    }

    protected SessionStorage sessionStorage() {
        return new SessionStorage();
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
