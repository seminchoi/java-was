package codesquad.server.router;

import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;

import java.util.function.Function;

public class RouteEntry {
    private final HttpMethod method;
    private final String path;
    private final Function<HttpRequest, Object> handler;
    private final String[] pathSegments;
    private final boolean[] isDynamicSegment;

    public RouteEntry(HttpMethod method, String path, Function<HttpRequest, Object> handler) {
        this.method = method;
        this.path = path;
        this.pathSegments = path.split("/");
        this.isDynamicSegment = new boolean[pathSegments.length];
        this.handler = handler;

        for (int i = 0; i < pathSegments.length; i++) {
            if (pathSegments[i].startsWith("{") && pathSegments[i].endsWith("}")) {
                isDynamicSegment[i] = true;
                pathSegments[i] = pathSegments[i].substring(1, pathSegments[i].length() - 1);
            }
        }
    }

    public static class Builder {
        private HttpMethod method;
        private String path;
        private Function<HttpRequest, Object> handler;

        public Builder() {}

        public Builder route(HttpMethod method, String path) {
            this.method = method;
            this.path = path;
            return this;
        }

        public Builder handler(Function<HttpRequest, Object> handler) {
            this.handler = handler;
            return this;
        }

        public RouteEntry build() {
            return new RouteEntry(method, path, handler);
        }
    }

    public boolean matches(HttpRequest request) {
        if (method != request.getMethod()) {
            return false;
        }

        String[] requestPathSegments = request.getPath().split("/");

        if (pathSegments.length != requestPathSegments.length) {
            return false;
        }

        for (int i = 0; i < pathSegments.length; i++) {
            if (!isDynamicSegment[i] && !pathSegments[i].equals(requestPathSegments[i])) {
                return false;
            }
        }
        return true;
    }

    public Function<HttpRequest, Object> getHandler() {
        return handler;
    }
}
