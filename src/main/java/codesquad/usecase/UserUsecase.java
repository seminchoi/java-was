package codesquad.usecase;

import codesquad.domain.user.User;
import codesquad.domain.UserStorage;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

public class UserUsecase {
    private final UserStorage userStorage;

    public UserUsecase(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public HttpResponse createUser(HttpRequest request) {
        String userId = request.getParam("userId");
        String password = request.getParam("password");
        String name = request.getParam("name");

        User user = new User(userId, password, name);

        userStorage.saveUser(user);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.SEE_OTHER);
        httpResponse.writeHeader("Location", "/login/index.html");

        return httpResponse;
    }
}
