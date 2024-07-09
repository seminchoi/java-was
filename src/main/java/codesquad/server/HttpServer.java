package codesquad.server;

import codesquad.exception.HttpException;
import codesquad.server.http.HttpRequest;
import codesquad.server.http.HttpRequestParser;
import codesquad.server.http.HttpResponse;
import codesquad.server.router.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final RequestHandler requestHandler;

    public HttpServer(int port, int threadPoolSize, RequestHandler requestHandler) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.requestHandler = requestHandler;
    }

    public void service() throws IOException {
        logger.info("Listening for connection on port 8080 ....");
        while (true) {
            Socket clientSocket = serverSocket.accept();
            executorService.submit(() -> sendResponse(clientSocket));
        }
    }

    private void sendResponse(Socket clientSocket) {
        try {
            InputStream inputStream = clientSocket.getInputStream();
            HttpRequest httpRequest = HttpRequestParser.parseRequest(inputStream);
            logger.info(httpRequest.getPath());
            HttpResponse httpResponse;
            OutputStream outputStream = clientSocket.getOutputStream();
            try {
                httpResponse = requestHandler.handleRequest(httpRequest);
            } catch (HttpException e) {
                httpResponse = HttpResponse.fromHttpException(e);
            }

            outputStream.write(httpResponse.makeResponse());
            outputStream.flush();

        } catch (IOException e) {
            logger.error(e.getMessage());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        } finally {
            try {
                clientSocket.close();
                logger.debug("socket closed");
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }
}
