package codesquad.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpMethod method;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> headers = new HashMap<>();

    public HttpRequest(List<String> request) {
        String[] requestLineArgs = request.get(0).split(" ");

        //TODO: 요청이 올바르지 않을때 예외처리
        this.method = HttpMethod.valueOf(requestLineArgs[0]);
        this.uri = requestLineArgs[1];
        this.httpVersion = requestLineArgs[2];

        parseRequestHeader(request);
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

    public String getUri() {
        return uri;
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
