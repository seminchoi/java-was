package codesquad.server;

import codesquad.server.handler.SocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer {
    private static final Logger logger = LoggerFactory.getLogger(HttpServer.class);

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final SocketHandler socketHandler;
    
    private boolean active = true; 
    
    public HttpServer(int port, int threadPoolSize, SocketHandler socketHandler) throws IOException {
        this.serverSocket = new ServerSocket(port);
        this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        this.socketHandler = socketHandler;
    }

    public void service() {
        logger.info("Listening for connection on port 8080 ....");
        startServer();
    }

    private void startServer() {
        while (active) {
            try {
                if(Thread.currentThread().isInterrupted()) {
                    break;
                }
                Socket clientSocket = serverSocket.accept();
                executorService.execute(() -> socketHandler.handle(clientSocket));
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public void stop() {
        active = false;
        executorService.shutdown();
    }
}
