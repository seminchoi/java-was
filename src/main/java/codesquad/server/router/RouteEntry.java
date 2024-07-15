package codesquad.server.router;

import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;

import java.util.function.Function;

public class RouteEntry {
    private final HttpMethod method;
    private final String path;
    private final Function<HttpRequest, Object> handler;

    public RouteEntry(HttpMethod method, String path, Function<HttpRequest, Object> handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
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
        return method == request.getMethod() && path.equals(request.getPath());
    }

    public Function<HttpRequest, Object> getHandler() {
        return handler;
    }
}
