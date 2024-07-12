package codesquad.app.config;

import codesquad.app.storage.UserStorage;
import codesquad.server.handler.RequestHandler;
import codesquad.app.usecase.UserUsecase;
import codesquad.server.handler.SocketHandler;

public class ObjectContainer {
    private static final ObjectFactory objectFactory = new ObjectFactory();
    private static final UserUsecase userUsecase = objectFactory.userUsecase();
    private static final UserStorage userStorage = objectFactory.userStorage();
    private static final RequestHandler requestHandler = objectFactory.requestHandler();
    private static final SocketHandler socketHandler = objectFactory.socketHandler();

    public static UserUsecase userUsecase() {
        return userUsecase;
    }

    public static RequestHandler requestHandler() {
        return requestHandler;
    }

    public static SocketHandler socketHandler() {
        return socketHandler;
    }

    public static UserStorage userStorage() {
        return userStorage;
    }
}
