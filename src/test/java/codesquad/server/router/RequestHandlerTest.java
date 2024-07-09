package codesquad.server.router;

import codesquad.exception.HttpException;
import codesquad.server.http.HttpMethod;
import codesquad.server.http.HttpRequest;
import codesquad.server.http.HttpResponse;
import codesquad.server.http.HttpStatus;
import codesquad.util.HttpRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHandlerTest {

    private RequestHandler requestHandler;

    @BeforeEach
    void setUp() {
        StaticFilePathManager staticFilePathManager = new StaticFilePathManager();
        StaticFileProcessor staticFileProcessor = new StaticFileProcessor(staticFilePathManager);
        requestHandler = new RequestHandler(new TestRouteEntryManager(), staticFileProcessor);
    }

    @Test
    void GET_요청이_파일일_때_파일이_있으면_파일을_읽어서_반환한다() throws URISyntaxException {
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest("/hello.hi.html");

        HttpResponse httpResponse = requestHandler.handleRequest(httpRequest);
        String response = new String(httpResponse.makeResponse());

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<!DOCTYPE html>"));
    }

    @Test
    void GET_요청이_파일일_때_파일이_없으면_NOT_FOUND를_반환한다() throws URISyntaxException {
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest("/hello.html");

        assertThatThrownBy(() -> requestHandler.handleRequest(httpRequest))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void 요청_URI가_슬래쉬로_끝나면_슬래쉬를_제거한_URI로_리다이렉트_한다() throws URISyntaxException {
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest("/hello.hi.html/");

        HttpResponse httpResponse = requestHandler.handleRequest(httpRequest);
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("HTTP/1.1 303 See Other");
        assertThat(response).contains("Location: /hello.hi.html");
    }

    @Test
    void 요청_URI가_RouteEntryManager에_등록되어_있으면_요청을_핸들링한다() throws URISyntaxException {
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest("/hello");

        HttpResponse httpResponse = requestHandler.handleRequest(httpRequest);
        String response = new String(httpResponse.makeResponse());

        assertTrue(response.contains("HTTP/1.1 200 OK"));
    }

    private static class TestRouteEntryManager extends RouteEntryManager {
        @Override
        public List<RouteEntry> getRouteEntry() {
            return List.of(
                    new RouteEntry.Builder().route(HttpMethod.GET, "/hello")
                            .handler(httpRequest -> new HttpResponse(HttpStatus.OK)).build());
        }
    }
}