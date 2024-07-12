package codesquad.app.config;

import codesquad.app.storage.UserStorage;
import codesquad.app.usecase.UserUsecase;
import codesquad.http.security.SessionStorage;

public class AppContainer {
    private static class UserUsecaseHolder {
        private static final UserUsecase INSTANCE = new UserUsecase(UserStorageHolder.INSTANCE, SessionStorageHolder.INSTANCE);
    }

    private static class UserStorageHolder {
        private static final UserStorage INSTANCE = new UserStorage();
    }

    private static class SessionStorageHolder {
        private static final SessionStorage INSTANCE = new SessionStorage();
    }

    public static UserUsecase userUsecase() {
        return UserUsecaseHolder.INSTANCE;
    }

    public static UserStorage userStorage() {
        return UserStorageHolder.INSTANCE;
    }

    public static SessionStorage sessionStorage() {
        return SessionStorageHolder.INSTANCE;
    }
}
