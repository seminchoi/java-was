package codesquad.http;

import codesquad.config.StaticFileProcessor;
import codesquad.config.RouteEntry;
import codesquad.config.RouteEntryManager;
import codesquad.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final RouteEntryManager routeEntryManager;
    private final StaticFileProcessor staticFileProcessor;

    public RequestHandler(RouteEntryManager routeEntryManager, StaticFileProcessor staticFileProcessor) {
        this.routeEntryManager = routeEntryManager;
        this.staticFileProcessor = staticFileProcessor;
    }

    public HttpResponse handleRequest(HttpRequest httpRequest) {
        for (RouteEntry entry : routeEntryManager.getRouteEntry()) {
            if(entry.matches(httpRequest)) {
                HttpResponse response = entry.getHandler().apply(httpRequest);
                if(response != null) {
                    logger.debug("response");
                    return response;
                }
            }
        }

        HttpResponse httpResponse = staticFileProcessor.readFile(httpRequest.getPath());
        if(httpResponse != null) {
            return httpResponse;
        }

        throw new HttpException(HttpStatus.NOT_FOUND);
    }
}
