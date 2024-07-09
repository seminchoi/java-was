package codesquad.util;

import codesquad.server.http.HttpMethod;
import codesquad.server.http.HttpRequest;

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

    public static HttpRequest createHttpRequest(HttpMethod httpMethod, String requestUri, Map<String, String> headers) throws URISyntaxException {
        URI uri = new URI(requestUri);
        return new HttpRequest(httpMethod, uri, "HTTP/1.1", headers);
    }
}
