package codesquad.server.handler;

import codesquad.TestSocket;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class SocketHandlerTest {
    private MockRequestHandler mockRequestHandler;
    private SocketHandler socketHandler;

    @BeforeEach
    public void setUp() {
        mockRequestHandler = new MockRequestHandler();
        socketHandler = new SocketHandler(mockRequestHandler);
    }

    @Test
    public void handler가_response를_생성하면_소켓_OutputStream에_응답을_작성한다() {
        TestSocket testSocket = new TestSocket();
        String httpRequestContent = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        testSocket.setInputStreamContent(httpRequestContent);
        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        mockRequestHandler.setHttpResponse(httpResponse);

        socketHandler.handle(testSocket);

        Assertions.assertThat(testSocket.getOutputStreamContent()).contains("HTTP/1.1 200 OK\r\n");
    }

    @Test
    public void handler가_예외를_발생하면_소켓_OutputStream에_예외_타입에_맞는_응답을_작성한다() {
        TestSocket testSocket = new TestSocket();
        String httpRequestContent = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        testSocket.setInputStreamContent(httpRequestContent);

        mockRequestHandler.setThrowException(true);
        socketHandler.handle(testSocket);

        Assertions.assertThat(testSocket.getOutputStreamContent()).contains("HTTP/1.1 404 Not Found\r\n");

    }

    private static class MockRequestHandler extends RequestHandler {

        private HttpResponse httpResponse;
        private boolean throwException = false;

        public MockRequestHandler() {
            super(null, null, null); // 부모 클래스의 생성자를 호출, 필요한 매개변수는 null로 설정
        }

        public void setHttpResponse(HttpResponse response) {
            this.httpResponse = response;
        }

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        @Override
        public HttpResponse handleRequest(HttpRequest httpRequest) {
            if (throwException) {
                throw new HttpException(HttpStatus.NOT_FOUND);
            }
            return httpResponse;
        }
    }

}
