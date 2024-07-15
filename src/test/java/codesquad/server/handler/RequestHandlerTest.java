package codesquad.server.handler;

import codesquad.container.Container;
import codesquad.container.ContainerConfigurer;
import codesquad.container.ContainerHolder;
import codesquad.exception.HttpException;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.server.router.RouteEntry;
import codesquad.server.router.RouteEntryManager;
import codesquad.server.router.StaticFilePathManager;
import codesquad.util.HttpRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHandlerTest {
    private static ContainerConfigurer containerConfigurer = new ContainerConfigurer() {
        @Override
        public List<String> getTargetPackages() {
            return List.of("codesquad.server");
        }

        @Override
        public void addTargetPackages(String... targetPackages) {
        }
    };

    private RequestHandler requestHandler;


    @BeforeEach
    void setUp() {

        ContainerHolder.init(containerConfigurer);
        Container container = ContainerHolder.getContainer();
        RouteEntryManager routeEntryManager = (RouteEntryManager) container.getComponent("routeEntryManager");
        routeEntryManager.add(new RouteEntry.Builder().route(HttpMethod.GET, "/hello")
                .handler(httpRequest -> new HttpResponse(HttpStatus.OK))
                .build()
        );
        StaticFilePathManager staticFilePathManager = (StaticFilePathManager) container.getComponent("staticFilePathManager");
        staticFilePathManager.addPath("static");

        requestHandler = (RequestHandler) container.getComponent("requestHandler");
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
}