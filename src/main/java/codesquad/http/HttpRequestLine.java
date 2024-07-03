package codesquad.http;

public class HttpRequestLine {
    private final HttpMethod method;
    private final String uri;
    private final String httpVersion;

    public HttpRequestLine(String requestLine) {
        String[] requestLineArgs = requestLine.split(" ");

        this.method = HttpMethod.valueOf(requestLineArgs[0]);
        this.uri = requestLineArgs[1];
        this.httpVersion = requestLineArgs[2];
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
