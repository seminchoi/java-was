package codesquad.http;

import codesquad.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpMethod method;
    private final URI uri;
    private final Map<String, String> queryParams = new HashMap<>();
    private final String httpVersion;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(List<String> request) {
        if(request.isEmpty()) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        String[] requestLineArgs = request.get(0).split(" ");

        //TODO: 요청이 올바르지 않을때 예외처리
        this.method = HttpMethod.valueOf(requestLineArgs[0]);
        try {
            this.uri = new URI(requestLineArgs[1]);
        } catch (URISyntaxException e) {
            logger.info(e.getMessage());
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        parseQueryParams();
        this.httpVersion = requestLineArgs[2];

        parseRequestHeader(request);
    }

    private void parseQueryParams() {
        String query = this.uri.getQuery();
        if (query == null) {
            return;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] splitPair = pair.split("=");
            if (queryParams.containsKey(splitPair[0])) {
                queryParams.put(splitPair[0], queryParams.get(splitPair[0]) + "," + splitPair[1]);
                continue;
            }
            queryParams.put(splitPair[0], splitPair[1]);
        }
    }

    private void parseRequestHeader(final List<String> request) {
        for (int line = 1; line < request.size() && !request.get(line).equals("\r\n"); line++) {
            String[] requestHeaderArgs = request.get(line).split(":");
            try {
                headers.put(requestHeaderArgs[0].trim(), requestHeaderArgs[1].trim());
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("{} 리퀘스트 헤더에 디렉티브가 없습니다.", request.get(line));
            }
        }
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
}
