package codesquad;

import codesquad.app.config.HttpConfig;
import codesquad.container.Container;
import codesquad.container.ContainerHolder;
import codesquad.container.WebContainerConfigurer;
import codesquad.server.HttpServer;
import codesquad.server.handler.SocketHandler;

import java.io.IOException;


public class Main {
    public static void main(String[] args) throws IOException {
        ContainerHolder.init(new WebContainerConfigurer());
        Container container = ContainerHolder.getContainer();

        HttpConfig httpConfig = new HttpConfig();
        httpConfig.config();

        HttpServer httpServer = new HttpServer(8080, 10, (SocketHandler) container.getComponent("socketHandler"));
        httpServer.service();
    }
}
