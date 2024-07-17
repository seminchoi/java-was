package codesquad.app.service;

import codesquad.app.model.User;
import codesquad.app.storage.UserDao;
import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpStatus;
import codesquad.http.security.Session;
import codesquad.http.security.SessionStorage;

import java.util.Optional;

@Component
public class SessionService {
    private final SessionStorage sessionStorage;
    private final UserDao userDao;

    public SessionService(SessionStorage sessionStorage, UserDao userDao) {
        this.sessionStorage = sessionStorage;
        this.userDao = userDao;
    }

    public boolean isAuthenticated(Session session) {
        return session != null && !session.isExpired();

    }

    public User getUser(Session session) {
        return userDao.findById((String) session.getIdentity()).orElseThrow(() ->
                new HttpException(HttpStatus.CONFLICT)
        );
    }


    public Session getSession(HttpRequest httpRequest) {
        String sessionId = httpRequest.getCookie("SID");

        if (sessionId == null) {
            return null;
        }

        Optional<Session> session = sessionStorage.find(sessionId);
        return session.orElse(null);
    }
}
