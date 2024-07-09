package codesquad.http;

import codesquad.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parseRequest(final InputStream inputStream) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest httpRequest = parseHeader(bufferedReader);
            writeBody(bufferedReader, httpRequest);
            return httpRequest;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static HttpRequest parseHeader(final BufferedReader bufferedReader) throws IOException {
        List<String> request = new ArrayList<>();

        String line;
        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            request.add(line);
        }

        if (request.isEmpty()) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("request header: {}", request);

        String[] requestLineArgs = request.get(0).split(" ");
        HttpMethod httpMethod = HttpMethod.valueOf(requestLineArgs[0]);
        URI uri;
        try {
            uri = new URI(requestLineArgs[1]);
        } catch (URISyntaxException e) {
            logger.info(e.getMessage());
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        String httpVersion = requestLineArgs[2];

        Map<String, String> headers = parseRequestHeader(request);

        return new HttpRequest(httpMethod, uri, httpVersion, headers);
    }

    private static Map<String, String> parseRequestHeader(final List<String> request) {
        Map<String, String> headers = new HashMap<>();
        for (int line = 1; line < request.size() && !request.get(line).equals("\r\n"); line++) {
            String[] requestHeaderArgs = request.get(line).split(":");
            try {
                headers.put(requestHeaderArgs[0].trim(), requestHeaderArgs[1].trim());
            } catch (ArrayIndexOutOfBoundsException e) {
                logger.error("{} 리퀘스트 헤더에 디렉티브가 없습니다.", request.get(line));
            }
        }

        return headers;
    }

    private static void writeBody(final BufferedReader bufferedReader, final HttpRequest httpRequest) throws IOException {
        int contentLength = httpRequest.getContentLength();

        if(contentLength <= 0) {
            return;
        }

        char[] body = new char[contentLength];
        bufferedReader.read(body);

        String decodedBody = URLDecoder.decode(new String(body), "UTF-8");
        logger.info(decodedBody);
        httpRequest.writeBody(decodedBody);
    }
}
