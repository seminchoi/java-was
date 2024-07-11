package codesquad.server.http;

import codesquad.exception.HttpException;
import codesquad.server.structure.MultiValueMap;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

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
    private MultiValueMap<String, String> headers = new MultiValueMap<>();
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

    public void setCookie(String value) {
        headers.put("Set-Cookie", value);
    }

    public void writeBody(byte[] body) {
        this.body = body;
        headers.put("Content-Length", body.length + "");
    }

    public List<String> getHeader(String key) {
        return headers.get(key);
    }

    public byte[] makeResponse() {
        byte[] header = makeHeader();

        if (body == null) {
            return header;
        }

        byte[] response = new byte[header.length + body.length];

        System.arraycopy(header, 0, response, 0, header.length);
        System.arraycopy(body, 0, response, header.length, body.length);

        return response;
    }

    private byte[] makeHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append(addCrlf(makeResponseLine()));
        for (String key : headers.keySet()) {
            List<String> values = headers.get(key);
            for (String value : values) {
                sb.append(key).append(": ")
                        .append((value)).append(crlf());
            }
        }
        sb.append(crlf());
        return sb.toString().getBytes();
    }

    private String makeResponseLine() {
        return httpVersion + " " + httpStatus.getStatusCode() + " " + httpStatus.getStatus();
    }
}
