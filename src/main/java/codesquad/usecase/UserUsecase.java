package codesquad.usecase;

import codesquad.exception.HttpException;
import codesquad.model.User;
import codesquad.security.Session;
import codesquad.security.SessionStorage;
import codesquad.server.http.*;
import codesquad.server.structure.Params;
import codesquad.storage.UserStorage;

import java.util.Optional;

public class UserUsecase {
    private final UserStorage userStorage;
    private final SessionStorage sessionStorage;

    public UserUsecase(UserStorage userStorage, SessionStorage sessionStorage) {
        this.userStorage = userStorage;
        this.sessionStorage = sessionStorage;
    }

    public HttpResponse register(HttpRequest httpRequest) {
        String body = httpRequest.getBody();

        Params params = new Params(body);

        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");

        User user = new User(userId, password, name);

        userStorage.saveUser(user);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/");

        return httpResponse;
    }

    public HttpResponse login(HttpRequest httpRequest) {
        String body = httpRequest.getBody();

        Params params = new Params(body);

        String userId = params.get("userId");
        String password = params.get("password");

        User user = authenticate(userId, password);
        if (user == null) {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
            httpResponse.writeHeader("Location", "/login/login_failed.html");

            return httpResponse;
        }

        Session session = new Session(user);
        sessionStorage.save(session);

        Cookie cookie = new Cookie.Builder("SID", session.getSessionId())
                .maxAge(session.getMaxAge()).build();

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/main");
        httpResponse.setCookie(cookie.makeHeaderLine());

        return httpResponse;
    }

    private User authenticate(String userId, String password) {
        Optional<User> optionalUser = userStorage.findByUserId(userId);
        if (optionalUser.isEmpty()) {
            return null;
        }
        User user = optionalUser.get();
        if (!user.getPassword().equals(password)) {
            return null;

        }
        return user;
    }

    public HttpResponse logout(HttpRequest httpRequest) {
        String sessionId = httpRequest.getCookie("SID");

        if (sessionId == null) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }

        Session session = sessionStorage.find(sessionId).orElseThrow(
                () -> new HttpException(HttpStatus.UNAUTHORIZED)
        );

        sessionStorage.remove(session.getSessionId());

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/");
        return httpResponse;
    }
}
