package codesquad.usecase;

import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.Params;
import codesquad.model.User;
import codesquad.storage.UserStorage;

public class UserUsecase {
    private final UserStorage userStorage;

    public UserUsecase(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public HttpResponse createUser(HttpRequest httpRequest) {
        String body = httpRequest.getBody();
        Params params = new Params(body);

        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");

        User user = new User(userId, password, name);

        userStorage.saveUser(user);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.SEE_OTHER);
        httpResponse.writeHeader("Location", "/");

        return httpResponse;
    }
}
