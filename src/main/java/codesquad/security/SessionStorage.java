package codesquad.security;

import codesquad.model.User;

import java.util.HashMap;
import java.util.Map;

public class SessionStorage {
    private final Map<Session, User> sessions = new HashMap<>();
    private final Map<User, Session> reverseSessions = new HashMap<>();


    public void save(Session session, User user) {
        if(reverseSessions.containsKey(user)) {
            Session oldSession = reverseSessions.get(user);
            reverseSessions.remove(user);
            sessions.remove(oldSession);
        }
        sessions.put(session, user);
    }

    public User getUser(Session session) {
        return sessions.get(session);
    }
}
