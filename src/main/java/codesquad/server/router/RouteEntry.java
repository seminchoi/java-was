package codesquad.server.router;

import codesquad.server.http.HttpMethod;
import codesquad.server.http.HttpRequest;
import codesquad.server.http.HttpResponse;

import java.util.function.Function;

public class RouteEntry {
    private final HttpMethod method;
    private final String path;
    private final Function<HttpRequest, HttpResponse> handler;

    public RouteEntry(HttpMethod method, String path, Function<HttpRequest, HttpResponse> handler) {
        this.method = method;
        this.path = path;
        this.handler = handler;
    }

    public static class Builder {
        private HttpMethod method;
        private String path;
        private Function<HttpRequest, HttpResponse> handler;

        public Builder() {}

        public Builder route(HttpMethod method, String path) {
            this.method = method;
            this.path = path;
            return this;
        }

        public Builder handler(Function<HttpRequest, HttpResponse> handler) {
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

    public Function<HttpRequest, HttpResponse> getHandler() {
        return handler;
    }
}