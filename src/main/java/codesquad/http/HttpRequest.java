package codesquad.http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private static Map<String, String> headers = new HashMap<>();

    public HttpRequest(final InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String request = reader.readLine();
        logger.info(request.toString());
        parseRequestLine(request);

        if (reader.ready()) {
            parseRequestHeader(reader.readLine());
        }
    }

    private void parseRequestLine(final String requestLine) {
        String[] requestLineArgs = requestLine.split(" ");

        headers.put("Method", requestLineArgs[0]);
        headers.put("Uri", requestLineArgs[1]);
        headers.put("HttpVersion", requestLineArgs[2]);
    }

    private void parseRequestHeader(final String requestHeader) {
        String[] requestHeaderArgs = requestHeader.split(":");
        try {
            headers.put(requestHeaderArgs[0], requestHeaderArgs[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            logger.error(requestHeader);
        }
    }

    public HttpMethod getMethod() {
        return HttpMethod.valueOf(headers.get("Method"));
    }

    public String getUri() {
        return headers.get("Uri");
    }

    public String getHttpVersion() {
        return headers.get("HttpVersion");
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
}
