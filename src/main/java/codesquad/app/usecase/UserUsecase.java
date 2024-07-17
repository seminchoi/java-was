package codesquad.app.usecase;

import codesquad.app.model.User;
import codesquad.app.storage.UserDao;
import codesquad.container.Component;
import codesquad.exception.DbConstraintException;
import codesquad.exception.HttpException;
import codesquad.http.Cookie;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.security.Session;
import codesquad.http.security.SessionStorage;
import codesquad.http.structure.Params;
import codesquad.template.DynamicHtml;

import java.util.List;
import java.util.Optional;

@Component
public class UserUsecase {
    private final UserDao userDao;
    private final SessionStorage sessionStorage;

    public UserUsecase(UserDao userDao, SessionStorage sessionStorage) {
        this.userDao = userDao;
        this.sessionStorage = sessionStorage;
    }

    public HttpResponse register(HttpRequest httpRequest) {
        String body = httpRequest.getBody();

        Params params = new Params(body);

        String userId = params.get("userId");
        String password = params.get("password");
        String name = params.get("name");

        User user = new User(userId, password, name);

        try {
            userDao.save(user);
        } catch (DbConstraintException e) {
            throw new HttpException(HttpStatus.CONFLICT, "아이디 중복입니다. 다른 아이디를 사용해주세요.");
        }

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
        Optional<User> optionalUser = userDao.findById(userId);
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

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);

        if (session != null) {
            sessionStorage.remove(session.getSessionId());
            Cookie cookie = new Cookie.Builder("SID", session.getSessionId())
                    .maxAge(0).build();
            httpResponse.setCookie(cookie.makeHeaderLine());
        }

        httpResponse.writeHeader("Location", "/");
        return httpResponse;
    }

    public Object userList(HttpRequest httpRequest) {
        Session session = getSession(httpRequest);
        if (session == null || session.isExpired()) {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
            httpResponse.writeHeader("Location", "/login");

            return httpResponse;
        }

        DynamicHtml dynamicHtml = new DynamicHtml();
        dynamicHtml.setTemplate("/user/user_list.html");
        List<User> users = userDao.findAll();
        dynamicHtml.setArg("users", users);

        return dynamicHtml;
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
