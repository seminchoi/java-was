package codesquad.server.router;

import codesquad.app.usecase.UserUsecase;
import codesquad.container.Component;
import codesquad.container.ContainerHolder;
import codesquad.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteEntryManager {
    private final List<RouteEntry> routeEntry;

    public RouteEntryManager(RouteEntryConfigurer routeEntryConfigurer) {
        routeEntry = routeEntryConfigurer.getRouteEntries();
    }

    public List<RouteEntry> getRouteEntry() {
        return routeEntry;
    }
}
