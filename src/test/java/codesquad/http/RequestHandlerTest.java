package codesquad.http;

import codesquad.TestStaticFilePathManager;
import codesquad.server.RequestHandler;
import codesquad.server.RouteEntryManager;
import codesquad.server.StaticFileProcessor;
import codesquad.exception.HttpException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHandlerTest {

    private RequestHandler requestHandler;

    @BeforeEach
    void setUp() {
        TestStaticFilePathManager testStaticFilePathManager = new TestStaticFilePathManager();
        StaticFileProcessor staticFileProcessor = new StaticFileProcessor(testStaticFilePathManager);
        requestHandler = new RequestHandler(new RouteEntryManager(), staticFileProcessor);
    }

    @Test
    void GET_요청이_파일일_때_파일이_있으면_파일을_읽어서_반환한다() {
        HttpRequest httpRequest = new HttpRequest(List.of(
                "GET /hello.hi.html HTTP/1.1"
        ));

        HttpResponse httpResponse = requestHandler.handleRequest(httpRequest);
        String response = new String(httpResponse.makeResponse());

        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<!DOCTYPE html>"));
    }

    @Test
    void GET_요청이_파일일_때_파일이_없으면_NOT_FOUND를_반환한다() {
        HttpRequest httpRequest = new HttpRequest(List.of(
                "GET /hello.html HTTP/1.1"
        ));

        assertThatThrownBy(() -> requestHandler.handleRequest(httpRequest))
                .isInstanceOf(HttpException.class);
    }
}