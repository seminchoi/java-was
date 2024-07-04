package codesquad.config;

import codesquad.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class RouteEntryManager {
    private final List<RouteEntry> routeEntry = new ArrayList<>();

    public RouteEntryManager() {
        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/user/create")
                        .handler((httpRequest) -> ObjectContainer.getUserUsecase().createUser(httpRequest))
                        .build()
        );
        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/registration")
                        .handler((httpRequest) -> ObjectContainer.getStaticResourceMappingUsecase().register())
                        .build()
        );
    }

    public List<RouteEntry> getRouteEntry() {
        return routeEntry;
    }
}
