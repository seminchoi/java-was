package codesquad.http.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class SessionStorage {
    private final Map<String, Session> sessions = new ConcurrentHashMap<>();


    public void save(Session session) {
        sessions.put(session.getSessionId(), session);
    }

    public void remove(String sessionId) {
        sessions.remove(sessionId);
    }

    public Optional<Session> find(String sessionId) {
        return Optional.ofNullable(sessions.get(sessionId));
    }
}
