package codesquad.security;

import java.util.UUID;

public class Session {
    private static final long DEFAULT_SESSION_TIMEOUT_MILLIS = 7 * 24 * 60 * 60 * 1000;
    private final String sessionId;
    private final long createdAt;

    public Session() {
        this.sessionId = UUID.randomUUID().toString();
        createdAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - createdAt > DEFAULT_SESSION_TIMEOUT_MILLIS;
    }

    public String getSessionId() {
        return sessionId;
    }
}
