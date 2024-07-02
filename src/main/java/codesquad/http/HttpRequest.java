package codesquad.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private HttpRequestLine requestLine;
    private Map<String, String> headers = new HashMap<>();

    public HttpRequest(List<String> request) {
        this.requestLine = new HttpRequestLine(request.get(0));
        parseRequestHeader(request);
    }


    private void parseRequestHeader(final List<String> request) {
        for (int line = 1; line < request.size() && !request.get(line).equals("\r\n"); line++) {
            String[] requestHeaderArgs = request.get(line).split(":");
            try {
                headers.put(requestHeaderArgs[0], requestHeaderArgs[1]);
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("{} 리퀘스트 헤더에 디렉티브가 없습니다.", request.get(line));
            }
        }
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getHttpVersion() {
        return requestLine.getHttpVersion();
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

    public boolean isValid() {
        return requestLine != null;
    }
}
