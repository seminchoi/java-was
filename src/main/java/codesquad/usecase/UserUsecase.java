package codesquad.usecase;

import codesquad.exception.HttpException;
import codesquad.model.User;
import codesquad.security.Session;
import codesquad.security.SessionStorage;
import codesquad.server.http.*;
import codesquad.storage.UserStorage;

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

        User user = userStorage.findByUserId(userId).orElseThrow(() -> new HttpException(HttpStatus.NOT_FOUND));
        if (!user.getPassword().equals(password)) {
            throw new HttpException(HttpStatus.NOT_FOUND);
        }

        Session session = new Session(user);
        sessionStorage.save(session);

        Cookie cookie = new Cookie.Builder("SID", session.getSessionId())
                .maxAge(7 * 24 * 60 * 60).build();


        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/main");
        httpResponse.setCookie(cookie.makeHeaderLine());

        return httpResponse;
    }

    public HttpResponse logout(HttpRequest httpRequest) {
        String sessionId = httpRequest.getCookie("SID");
        Session session = sessionStorage.find(sessionId).orElseThrow(
                () -> new HttpException(HttpStatus.UNAUTHORIZED)
        );

        sessionStorage.remove(session.getSessionId());

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/");
        return httpResponse;
    }
}
