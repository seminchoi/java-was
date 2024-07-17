package codesquad.http.security;

import codesquad.app.model.User;

import java.util.Objects;
import java.util.UUID;

public class Session {
    private static final long DEFAULT_SESSION_TIMEOUT_MILLIS = 7 * 24 * 60 * 60 * 1000L;
    private final String sessionId;
    private final long createdAt;
    private final Object identity;

    public Session(Object identity) {
        this.sessionId = UUID.randomUUID().toString();
        this.createdAt = System.currentTimeMillis();
        this.identity = identity;
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > DEFAULT_SESSION_TIMEOUT_MILLIS;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Object getIdentity() {
        return identity;
    }

    public long getMaxAge() {
        return DEFAULT_SESSION_TIMEOUT_MILLIS / 1000L;
    }




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Session session = (Session) o;
        return Objects.equals(sessionId, session.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(sessionId);
    }
}
