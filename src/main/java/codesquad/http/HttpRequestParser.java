package codesquad.http;

import codesquad.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpRequestParser {
    private static final int MAX_HEADER_SIZE = 8192;
    private static final int MAX_BODY_SIZE = 10 * 1024 * 1024;
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestParser.class);

    public static HttpRequest parseRequest(final InputStream inputStream) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] headerBytes = readHeader(bufferedInputStream);
            HttpRequest httpRequest = parseHeader(new String(headerBytes, StandardCharsets.UTF_8));
            byte[] bodyBytes = readBody(bufferedInputStream, httpRequest.getContentLength());
            httpRequest.writeBody(bodyBytes);
            return httpRequest;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private static byte[] readHeader(InputStream inputStream) throws IOException {
        ByteArrayOutputStream headerBytes = new ByteArrayOutputStream();
        int b;
        int readCount = 0;
        int consecutiveNewlines = 0;

        while ((b = inputStream.read()) != -1 && readCount < MAX_HEADER_SIZE) {
            readCount++;
            headerBytes.write(b);
            if (b == '\n') {
                consecutiveNewlines++;
                if (consecutiveNewlines == 2) {
                    break;
                }
            } else if (b != '\r') {
                consecutiveNewlines = 0;
            }
        }

        if (consecutiveNewlines != 2) {
            throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE);
        }

        logger.info(headerBytes.toString());
        return headerBytes.toByteArray();
    }

    private static HttpRequest parseHeader(String headerString) {
        String[] lines = headerString.split("\r\n");
        List<String> request = new ArrayList<>(Arrays.asList(lines));

        if (request.isEmpty()) {
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("request header: {}", request);

        String[] requestLineArgs = request.get(0).split(" ");
        if (requestLineArgs.length != 3) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }

        HttpMethod httpMethod;
        try {
            httpMethod = HttpMethod.valueOf(requestLineArgs[0]);
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }

        URI uri = parseRequestURI(requestLineArgs[1]);
        String httpVersion = requestLineArgs[2];

        Map<String, String> headers = parseRequestHeader(request);

        return new HttpRequest(httpMethod, uri, httpVersion, headers);
    }

    private static URI parseRequestURI(String requestURI) {
        try {
            return new URI(requestURI);
        } catch (URISyntaxException e) {
            logger.info(e.getMessage());
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
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


    private static byte[] readBody(InputStream inputStream, int contentLength) throws IOException {
        if (contentLength <= 0) {
            return new byte[0];
        }

        if (contentLength > MAX_BODY_SIZE) {
            throw new HttpException(HttpStatus.PAYLOAD_TOO_LARGE, "요청이 너무 큽니다.");
        }

        byte[] body = new byte[contentLength];
        int bytesRead = 0;
        int read;
        while ((read = inputStream.read(body, bytesRead, contentLength - bytesRead)) != 0) {
            if (read == -1) {
                break;
            }
            bytesRead += read;
        }

        if(inputStream.available() != 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "Body가 Content-Length보다 큽니다.");
        }


        return body;
    }
}
