package codesquad.http;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class HttpRequestTest {
    @Test
    public void Http_request_header를_읽고_해석할_수_있다() {
        List<String> header = List.of(
                "GET /index.html HTTP/1.1",
                "Host: www.example.com",
                "User-Agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
                "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
                "Accept-Language: en-US,en;q=0.5",
                "Accept-Encoding: gzip, deflate, br",
                "Connection: keep-alive",
                "Upgrade-Insecure-Requests: 1",
                "Cache-Control: max-age=0"
        );

        HttpRequest request = new HttpRequest(header);

        assertThat(request.getHost()).isEqualTo("www.example.com");
        assertThat(request.getConnection()).isEqualTo("keep-alive");
        assertThat(request.getAccept()).isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        assertThat(request.getHeader("Cache-Control")).isEqualTo("max-age=0");
    }
}
