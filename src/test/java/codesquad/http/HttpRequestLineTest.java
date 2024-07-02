package codesquad.http;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class HttpRequestLineTest {

    @Test
    public void http_request_line_문자열을_올바르게_파싱한다() {
        String requestLine = "GET / HTTP/1.1";
        HttpRequestLine httpRequestLine = new HttpRequestLine(requestLine);

        assertThat(httpRequestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(httpRequestLine.getUri()).isEqualTo("/");
        assertThat(httpRequestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
    }
}
