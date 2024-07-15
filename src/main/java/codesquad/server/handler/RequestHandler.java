package codesquad.server.handler;

import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.server.router.RouteEntry;
import codesquad.server.router.RouteEntryManager;
import codesquad.template.DynamicHtml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class RequestHandler {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private final RouteEntryManager routeEntryManager;
    private final StaticFileHandler staticFileHandler;

    public RequestHandler(RouteEntryManager routeEntryManager, StaticFileHandler staticFileHandler) {
        this.routeEntryManager = routeEntryManager;
        this.staticFileHandler = staticFileHandler;
    }

    public HttpResponse handleRequest(HttpRequest httpRequest) {
        if (httpRequest.isUrlEndingWithSlash()) {
            HttpResponse httpResponse = new HttpResponse(HttpStatus.SEE_OTHER);
            httpResponse.writeHeader("Location", httpRequest.getPath().substring(0, httpRequest.getPath().length() - 1));
            return httpResponse;
        }

        HttpResponse httpResponse = null;

        for (RouteEntry entry : routeEntryManager.getRouteEntries()) {
            if (entry.matches(httpRequest)) {
                Object response = entry.getHandler().apply(httpRequest);
                if(response instanceof DynamicHtml dynamicHtml) {
                    httpResponse = dynamicHtml.process();
                }
                if(response instanceof HttpResponse) {
                    httpResponse = (HttpResponse) response;
                }
                if (httpResponse != null) {
                    return httpResponse;
                }
            }
        }

        httpResponse = staticFileHandler.handle(httpRequest.getPath());
        if (httpResponse != null) {
            return httpResponse;
        }

        throw new HttpException(HttpStatus.NOT_FOUND);
    }
}
