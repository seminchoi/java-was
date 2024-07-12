package codesquad.server.router;

import codesquad.app.config.ObjectContainer;
import codesquad.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;

public class RouteEntryManager {
    private final List<RouteEntry> routeEntry = new ArrayList<>();

    public RouteEntryManager() {
        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/")
                        .handler((httpRequest) -> ObjectContainer.userUsecase().home(httpRequest))
                        .build()
        );

        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.POST, "/user/create")
                        .handler((httpRequest) -> ObjectContainer.userUsecase().register(httpRequest))
                        .build()
        );

        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.POST, "/login")
                        .handler((httpRequest) -> ObjectContainer.userUsecase().login(httpRequest))
                        .build()
        );

        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/logout")
                        .handler((httpRequest) -> ObjectContainer.userUsecase().logout(httpRequest))
                        .build()
        );

        routeEntry.add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/user/list")
                        .handler((httpRequest) -> ObjectContainer.userUsecase().userList(httpRequest))
                        .build()
        );
    }

    public List<RouteEntry> getRouteEntry() {
        return routeEntry;
    }
}
