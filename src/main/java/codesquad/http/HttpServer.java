package codesquad.http;

import codesquad.config.ResourcePathManager;

import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    private final ResourcePathManager resourcePathManager;
    private final RequestHandler requestHandler;

    public HttpServer(ResourcePathManager resourcePathManager) {
        this.resourcePathManager = resourcePathManager;
        this.requestHandler = new RequestHandler(resourcePathManager);
    }

    public void service(Socket clientSocket) {
        executorService.submit(() -> requestHandler.handleRequest(clientSocket));
    }
}
