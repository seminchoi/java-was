package codesquad.http;

import codesquad.http.structure.Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpMethod method;
    private final URI uri;
    private final Params queryParams;
    private final String httpVersion;
    private final Map<String, String> headers;

    private byte[] body;

    public HttpRequest(HttpMethod method, URI uri,
                       String httpVersion, Map<String, String> headers) {
        this.method = method;
        this.uri = uri;
        this.queryParams = new Params(uri.getQuery());
        this.httpVersion = httpVersion;
        this.headers = headers;
    }

    public void writeBody(byte[] body) {
        this.body = body;
    }

    public boolean isUrlEndingWithSlash() {
        if (getPath().equals("/")) {
            return false;
        }
        return uri.getPath().endsWith("/");
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getParam(String key) {
        return queryParams.get(key);
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getHost() {
        return headers.get("Host");
    }

    public String getConnection() {
        return headers.get("Connection");
    }

    public String getAccept() {
        return headers.get("Accept");
    }

    public String getHeader(String headerName) {
        return headers.get(headerName);
    }

    public String getCookie(String name) {
        if (!headers.containsKey("Cookie")) {
            return null;
        }

        String[] cookies = headers.get("Cookie").split(";");
        for (String cookie : cookies) {
            String[] pair = cookie.split("=");
            if (pair[0].trim().equals(name)) {
                return pair[1].trim();
            }
        }
        return null;
    }

    public int getContentLength() {
        return Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
    }

    public ContentType getContentType() {
        String contentTypeHeader = headers.get("Content-Type");
        if (contentTypeHeader == null) {
            return null;
        }

        int index = contentTypeHeader.indexOf(";");
        if (index == -1) {
            return ContentType.fromDirective(contentTypeHeader);
        }
        String contentTypeCore = contentTypeHeader.substring(0, index).trim();
        return ContentType.fromDirective(contentTypeCore);
    }

    public String getBoundary() {
        String contentTypeHeader = headers.get("Content-Type");
        if (contentTypeHeader == null) {
            return null;
        }

        int index = contentTypeHeader.indexOf("boundary=");
        if (index == -1) {
            return null;
        }

        String boundary = contentTypeHeader.substring(index + 9).trim();
        if (boundary.startsWith("\"") && boundary.endsWith("\"")) {
            boundary = boundary.substring(1, boundary.length() - 1);
        }

        return boundary;
    }


    public String getBody() {
        return new String(body);
    }

    public Params getBodyByUrlDecodedParams() {
        return new Params(body, getContentType(), getBoundary());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(uri.getPath()).append(" ").append(httpVersion).append("\r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }
        sb.append("\r\n");

        if (body != null && body.length > 0) {
            sb.append(new String(body));
        }

        return sb.toString();
    }
}
