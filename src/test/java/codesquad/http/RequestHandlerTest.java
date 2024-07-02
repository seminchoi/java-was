package codesquad.http;

import codesquad.TestResourcePathManager;
import codesquad.TestSocket;
import codesquad.config.ResourcePathManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestHandlerTest {

    private RequestHandler requestHandler;
    private TestSocket testSocket;

    @BeforeEach
    void setUp() {
        requestHandler = new RequestHandler(new TestResourcePathManager());
        testSocket = new TestSocket();
    }

    @Test
    void 요청이_null이면_BAD_REQUEST를_반환한다() {
        requestHandler.handleRequest(testSocket);

        String response = testSocket.getOutputStreamContent();
        assertTrue(response.contains("HTTP/1.1 400 BAD REQUEST"));
    }

    @Test
    void 요청이_비어있으면_BAD_REQUST를_반환한다() {
        testSocket.setInputStreamContent("\r\n");

        requestHandler.handleRequest(testSocket);

        String response = testSocket.getOutputStreamContent();
        assertTrue(response.contains("HTTP/1.1 400 BAD REQUEST"));
    }

    @Test
    void GET_요청이_파일일_때_파일이_있으면_파일을_읽어서_반환한다() {
        String request = "GET /hello.hi.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        testSocket.setInputStreamContent(request);

        requestHandler.handleRequest(testSocket);

        String response = testSocket.getOutputStreamContent();
        assertTrue(response.contains("HTTP/1.1 200 OK"));
        assertTrue(response.contains("Content-Type: text/html"));
        assertTrue(response.contains("<!DOCTYPE html>"));
    }

    @Test
    void GET_요청이_파일일_때_파일이_없으면_NOT_FOUND를_반환한다() {
        String request = "GET /hello.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        testSocket.setInputStreamContent(request);

        requestHandler.handleRequest(testSocket);

        String response = testSocket.getOutputStreamContent();
        assertTrue(response.contains("HTTP/1.1 404 NOT FOUND"));
    }
}