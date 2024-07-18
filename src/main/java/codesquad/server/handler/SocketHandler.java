package codesquad.server.handler;

import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpRequestParser;
import codesquad.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

@Component
public class SocketHandler {
    private static final Logger logger = LoggerFactory.getLogger(SocketHandler.class);

    private final RequestHandler requestHandler;

    public SocketHandler(RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void handle(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
            OutputStream outputStream = clientSocket.getOutputStream();

            HttpResponse httpResponse = requestHandler.handleRequest(httpRequest);
            outputStream.write(httpResponse.makeResponse());
            outputStream.flush();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}
