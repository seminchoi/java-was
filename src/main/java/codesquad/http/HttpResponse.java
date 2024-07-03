package codesquad.http;

import codesquad.exception.HttpException;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static codesquad.util.StringUtils.addCrlf;
import static codesquad.util.StringUtils.crlf;

public class HttpResponse {
    private static final String DEFAULT_CONNECTION_VALUE = "keep-alive";
    private static final String DEFAULT_HOST_VALUE = "localhost";
    private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";
    private static final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).withZone(ZoneId.of("GMT"));

    private String httpVersion;
    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(HttpStatus httpStatus) {
        httpVersion = DEFAULT_HTTP_VERSION;
        this.httpStatus = httpStatus;
        headers.put("Host", DEFAULT_HOST_VALUE);
        headers.put("Connection", DEFAULT_CONNECTION_VALUE);
        headers.put("Date", ZonedDateTime.now().format(formatter));
    }

    public static HttpResponse fromHttpException(HttpException httpException) {
        return new HttpResponse(httpException.getHttpStatus());
    }

    public void writeHeader(String key, String value) {
        headers.put(key, value);
    }

    public void writeBody(byte[] body) {
        this.body = body;
        headers.put("Content-Length", body.length + "");
    }

    public byte[] makeResponse() {
        byte[] header = makeHeader();
        byte[] response = new byte[header.length + body.length];

        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(body, 0, response, body.length, body.length);

        return response;
    }

    private byte[] makeHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(addCrlf(makeResponseLine()));
        for (Map.Entry<String, String> header : headers.entrySet()) {
            sb.append(header.getKey()).append(": ")
                    .append((header.getValue())).append(crlf());
        }
        sb.append(crlf());
        return sb.toString().getBytes();
    }

    private String makeResponseLine() {
        return httpVersion + " " + httpStatus.getStatus() + " " + httpStatus.getStatus();
    }
}
