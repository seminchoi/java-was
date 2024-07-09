package codesquad.server.router;

import codesquad.config.ObjectContainer;
import codesquad.server.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class RouteEntryManager {
    private final List<RouteEntry> routeEntry = new ArrayList<>();

    public RouteEntryManager() {
        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.POST, "/user/create")
                        .handler((httpRequest) -> ObjectContainer.getUserUsecase().register(httpRequest))
                        .build()
        );

        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.POST, "/login")
                        .handler((httpRequest) -> ObjectContainer.getUserUsecase().login(httpRequest))
                        .build()
        );
    }

    public List<RouteEntry> getRouteEntry() {
        return routeEntry;
    }
}
