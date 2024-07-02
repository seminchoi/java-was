package codesquad;

import codesquad.config.ResourcePathManager;
import codesquad.http.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        logger.info("Listening for connection on port 8080 ....");

        HttpServer httpServer = new HttpServer(new ResourcePathManager());

        while (true) { // 무한 루프를 돌며 클라이언트의 연결을 기다립니다.
            Socket clientSocket = serverSocket.accept(); // 클라이언트 연결을 수락합니다.
            httpServer.service(clientSocket);
        }
    }
}
