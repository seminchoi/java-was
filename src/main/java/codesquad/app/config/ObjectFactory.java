package codesquad.app.config;

import codesquad.http.security.SessionStorage;
import codesquad.app.storage.UserStorage;
import codesquad.server.handler.RequestHandler;
import codesquad.server.router.RouteEntryManager;
import codesquad.server.router.StaticFilePathManager;
import codesquad.server.handler.StaticFileHandler;
import codesquad.app.usecase.UserUsecase;

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

    protected StaticFileHandler resourcePathManager() {
        return new StaticFileHandler(staticFilePathManager());
    }

    protected StaticFilePathManager staticFilePathManager() {
        return new StaticFilePathManager();
    }
}
