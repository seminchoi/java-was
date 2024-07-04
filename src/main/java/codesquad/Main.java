package codesquad;

import codesquad.config.ObjectContainer;
import codesquad.http.HttpServer;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        HttpServer httpServer = new HttpServer(8080, 10, ObjectContainer.getRequestHandler());
        httpServer.service();
    }
}
