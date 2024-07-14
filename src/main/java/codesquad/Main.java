package codesquad;

import codesquad.container.Container;
import codesquad.container.WebContainerConfigurer;
import codesquad.server.HttpServer;
import codesquad.server.handler.SocketHandler;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        Container container = new Container(new WebContainerConfigurer());
        container.init();
        HttpServer httpServer = new HttpServer(8080, 10, (SocketHandler) container.getComponent("socketHandler"));
        httpServer.service();
    }
}
