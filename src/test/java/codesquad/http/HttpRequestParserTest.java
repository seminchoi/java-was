package codesquad.http;

import codesquad.exception.HttpException;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.http.HttpRequestParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class HttpRequestParserTest {
    @Test
    public void Http_request를_읽고_해석할_수_있다() {
        String header =
                "POST /user/create HTTP/1.1\r\n" +
                        "Host: www.example.com\r\n" +
                        "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36\r\n" +
                        "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8\r\n" +
                        "Accept-Language: en-US,en;q=0.5\r\n" +
                        "Accept-Encoding: gzip, deflate, br\r\n" +
                        "Connection: keep-alive\r\n" +
                        "Content-Length: 37\r\n" +
                        "\r\n" +
                        "userId=semin&name=semin&password=1234";

        InputStream inputStream = new ByteArrayInputStream(header.getBytes());

        HttpRequest request = HttpRequestParser.parseRequest(inputStream);

        assertThat(request.getMethod()).isEqualTo(HttpMethod.POST);
        assertThat(request.getPath()).isEqualTo("/user/create");
        assertThat(request.getHttpVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getHost()).isEqualTo("www.example.com");
        assertThat(request.getConnection()).isEqualTo("keep-alive");
        assertThat(request.getBody()).isEqualTo("userId=semin&name=semin&password=1234");
    }

    @Test
    public void Http_request가_비어있으면_HttpException이_발생한다() {
        String header = "";

        InputStream inputStream = new ByteArrayInputStream(header.getBytes());

        assertThatThrownBy(() -> HttpRequestParser.parseRequest(inputStream))
                .isInstanceOf(HttpException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Hello", "GOT / HTTP/1.1"})
    public void request_line의_문법이_올바르지_않으면_HttpException이_발생한다(String header) {
        InputStream inputStream = new ByteArrayInputStream(header.getBytes());

        assertThatThrownBy(() -> HttpRequestParser.parseRequest(inputStream))
                .isInstanceOf(HttpException.class);
    }
}
