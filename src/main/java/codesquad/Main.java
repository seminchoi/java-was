package codesquad;

import codesquad.app.config.ObjectContainer;
import codesquad.server.HttpServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8080, 10, ObjectContainer.getRequestHandler());
        httpServer.service();
    }
}
