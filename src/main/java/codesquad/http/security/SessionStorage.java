package codesquad.http.security;

import codesquad.container.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
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
