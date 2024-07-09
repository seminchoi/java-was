package codesquad.server.router;

import codesquad.server.http.HttpRequest;
import codesquad.server.http.HttpResponse;
import codesquad.server.http.HttpStatus;
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
        if (httpRequest.isUrlEndingWithSlash()) {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.SEE_OTHER);
            httpResponse.writeHeader("Location", httpRequest.getPath().substring(0, httpRequest.getPath().length() - 1));
            return httpResponse;
        }

        for (RouteEntry entry : routeEntryManager.getRouteEntry()) {
            if (entry.matches(httpRequest)) {
                HttpResponse response = entry.getHandler().apply(httpRequest);
                if (response != null) {
                    return response;
                }
            }
        }

        HttpResponse httpResponse = staticFileProcessor.readFile(httpRequest.getPath());
        if (httpResponse != null) {
            return httpResponse;
        }

        throw new HttpException(HttpStatus.NOT_FOUND);
    }
}