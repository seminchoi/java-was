package codesquad.server.http;

public class Cookie {
    private String name;
    private String value;
    private long maxAge;
    private String path;
    private boolean secure;
    private boolean httpOnly;
    private boolean partitioned;
    private SameSitePolicy sameSite;

    public Cookie(String name, String value, long maxAge, String path, boolean secure,
                  boolean httpOnly, boolean partitioned, SameSitePolicy sameSite) {
        this.name = name;
        this.value = value;
        this.maxAge = maxAge;
        this.path = path;
        this.secure = secure;
        this.httpOnly = httpOnly;
        this.partitioned = partitioned;
        this.sameSite = sameSite;
    }

    public String makeHeaderLine() {
        StringBuilder header = new StringBuilder();
        header.append(name).append("=").append(value);

        if (maxAge >= 0) {
            header.append("; Max-Age=").append(maxAge);
        }

        if (path != null && !path.isEmpty()) {
            header.append("; Path=").append(path);
        }

        if (secure) {
            header.append("; Secure");
        }

        if (httpOnly) {
            header.append("; HttpOnly");
        }

        if (partitioned) {
            header.append("; Partitioned");
        }

        if (sameSite != null) {
            header.append("; SameSite=").append(sameSite.getValue());
        }

        return header.toString();
    }

    public static class Builder {
        private String name;
        private String value;
        private long maxAge;
        private String path;
        private boolean secure;
        private boolean httpOnly;
        private boolean partitioned;
        private SameSitePolicy sameSite;

        public Builder(String name, String value) {
            this.name = name;
            this.value = value;
            this.maxAge = 34560000L;
            this.path = "/";
            this.secure = false;
            this.httpOnly = false;
            this.partitioned = false;
            this.sameSite = SameSitePolicy.LAX;
        }

        public Builder maxAge(long maxAge) {
            this.maxAge = maxAge;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder secure(boolean secure) {
            this.secure = secure;
            return this;
        }

        public Builder httpOnly(boolean httpOnly) {
            this.httpOnly = httpOnly;
            return this;
        }

        public Builder partitioned(boolean partitioned) {
            this.partitioned = partitioned;
            return this;
        }

        public Builder sameSite(SameSitePolicy sameSite) {
            this.sameSite = sameSite;
            return this;
        }

        public Cookie build() {
            return new Cookie(name, value, maxAge, path, secure, httpOnly, partitioned, sameSite);
        }
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public long getMaxAge() {
        return maxAge;
    }

    public String getPath() {
        return path;
    }

    public boolean isSecure() {
        return secure;
    }

    public boolean isHttpOnly() {
        return httpOnly;
    }

    public boolean isPartitioned() {
        return partitioned;
    }

    public SameSitePolicy getSameSite() {
        return sameSite;
    }
}
