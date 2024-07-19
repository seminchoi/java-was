package codesquad.util;

import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestUtil {
    public static HttpRequest createHttpRequest(HttpMethod method, String requestUri) throws URISyntaxException {
        return createHttpRequest(method, requestUri, new HashMap<>());
    }

    public static HttpRequest createHttpRequest(String requestUri) throws URISyntaxException {
        return createHttpRequest(HttpMethod.GET, requestUri, new HashMap<>());
    }

    public static HttpRequest createHttpRequest(HttpMethod method, String requestUri, Map<String, String> headers, String body) throws URISyntaxException {
        HttpRequest httpRequest = createHttpRequest(method, requestUri, headers);
        httpRequest.writeBody(body.getBytes());
        return httpRequest;
    }

    public static HttpRequest createHttpRequest(HttpMethod httpMethod, String requestUri, Map<String, String> headers) throws URISyntaxException {
        URI uri = new URI(requestUri);
        return new HttpRequest(httpMethod, uri, "HTTP/1.1", headers);
    }

    public static InputStream createHttpRequestInputStream(HttpMethod method, String requestUri) throws URISyntaxException {
        return createHttpRequestInputStream(method, requestUri, new HashMap<>());
    }

    public static InputStream createHttpRequestInputStream(String requestUri) throws URISyntaxException {
        return createHttpRequestInputStream(HttpMethod.GET, requestUri, new HashMap<>());
    }

    public static InputStream createHttpRequestInputStream(HttpMethod httpMethod, String requestUri, Map<String, String> headers) throws URISyntaxException {
        return createHttpRequestInputStream(httpMethod, requestUri, headers, "");
    }

    public static InputStream createHttpRequestInputStream(HttpMethod method, String requestUri, Map<String, String> headers, String body) throws URISyntaxException {
        HttpRequest httpRequest = new HttpRequest(method, new URI(requestUri),"HTTP/1.1", headers);
        httpRequest.writeBody(body.getBytes());
        return new ByteArrayInputStream(httpRequest.toString().getBytes());
    }
}
