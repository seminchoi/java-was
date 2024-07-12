package codesquad.app.usecase;

import codesquad.exception.HttpException;
import codesquad.template.DynamicHtml;
import codesquad.app.model.User;
import codesquad.http.security.Session;
import codesquad.http.security.SessionStorage;
import codesquad.file.AppFileReader;
import codesquad.http.Cookie;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.structure.Params;
import codesquad.app.storage.UserStorage;

import java.util.List;
import java.util.Optional;

public class UserUsecase {
    private final UserStorage userStorage;
    private final SessionStorage sessionStorage;

    public UserUsecase(UserStorage userStorage, SessionStorage sessionStorage) {
        this.userStorage = userStorage;
        this.sessionStorage = sessionStorage;
    }

    public HttpResponse home(HttpRequest httpRequest) {
        Session session = getSession(httpRequest);

        boolean isAuthenticated = session != null && !session.isExpired();
        DynamicHtml dynamicHtml = new DynamicHtml();
        dynamicHtml.setArg("authenticated", isAuthenticated);
        if(isAuthenticated) {
            String userId = session.getIdentity().toString();
            User user = userStorage.findByUserId(userId).get();
            dynamicHtml.setArg("user", user);
        }
        AppFileReader fileReader = new AppFileReader("static/index.html");
        String content = fileReader.getContent();
        String processed = dynamicHtml.process(content);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        httpResponse.writeBody(processed.getBytes());

        return httpResponse;
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

        Session session = new Session(user.getUserId());
        sessionStorage.save(session);

        Cookie cookie = new Cookie.Builder("SID", session.getSessionId())
                .maxAge(session.getMaxAge()).build();

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/");
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
        Session session = getSession(httpRequest);

        if(session == null) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }

        sessionStorage.remove(session.getSessionId());

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/");
        return httpResponse;
    }

    public HttpResponse userList(HttpRequest httpRequest) {
        Session session = getSession(httpRequest);
        if(session == null || session.isExpired()) {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
            httpResponse.writeHeader("Location", "/login");

            return httpResponse;
        }

        AppFileReader fileReader = new AppFileReader("static/user/user_list.html");
        String content = fileReader.getContent();

        DynamicHtml dynamicHtml = new DynamicHtml();
        List<User> users = userStorage.findAll();
        dynamicHtml.setArg("users", users);
        String html = dynamicHtml.process(content);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        httpResponse.writeBody(html.getBytes());

        return httpResponse;
    }

    private Session getSession(HttpRequest httpRequest) {
        String sessionId = httpRequest.getCookie("SID");

        if (sessionId == null) {
            return null;
        }

        Optional<Session> session = sessionStorage.find(sessionId);
        return session.orElse(null);
    }
}
