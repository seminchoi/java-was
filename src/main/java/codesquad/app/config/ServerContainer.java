package codesquad.app.config;

import codesquad.server.handler.RequestHandler;
import codesquad.server.handler.SocketHandler;
import codesquad.server.handler.StaticFileHandler;
import codesquad.server.router.RouteEntryManager;
import codesquad.server.router.StaticFilePathManager;

public class ServerContainer {
    private static class SocketHandlerHolder {
        private static final SocketHandler INSTANCE
                = new SocketHandler(RequestHandlerHolder.INSTANCE);
    }

    private static class RequestHandlerHolder {
        private static final RequestHandler INSTANCE
                = new RequestHandler(RouteEntryManagerHolder.INSTANCE, StaticFileHandlerHolder.INSTANCE);
    }

    private static class RouteEntryManagerHolder {
        private static final RouteEntryManager INSTANCE = new RouteEntryManager();
    }

    private static class StaticFileHandlerHolder {
        private static final StaticFileHandler INSTANCE = new StaticFileHandler(StaticFilePathManagerHolder.INSTANCE);
    }

    private static class StaticFilePathManagerHolder {
        private static final StaticFilePathManager INSTANCE = new StaticFilePathManager();
    }

    public static SocketHandler socketHandler() {
        return SocketHandlerHolder.INSTANCE;
    }

    public static RequestHandler requestHandler() {
        return RequestHandlerHolder.INSTANCE;
    }

    public static RouteEntryManager routeEntryManager() {
        return RouteEntryManagerHolder.INSTANCE;
    }

    public static StaticFileHandler staticFileHandler() {
        return StaticFileHandlerHolder.INSTANCE;
    }

    public static StaticFilePathManager staticFilePathManager() {
        return StaticFilePathManagerHolder.INSTANCE;
    }
}
