package codesquad.config;

import codesquad.storage.UserStorage;
import codesquad.server.RequestHandler;
import codesquad.usecase.UserUsecase;

public class ObjectContainer {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static final UserUsecase userUsecase = objectFactory.userUsecase();
    private static final UserStorage userStorage = objectFactory.userStorage();
    private static final RequestHandler requestHandler = objectFactory.requestHandler();

    public static UserUsecase getUserUsecase() {
        return userUsecase;
    }

    public static RequestHandler getRequestHandler() {
        return requestHandler;
    }

    public static UserStorage getUserStorage() {
        return userStorage;
    }
}
