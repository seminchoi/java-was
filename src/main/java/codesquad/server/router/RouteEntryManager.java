package codesquad.server.router;

import codesquad.container.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RouteEntryManager {
    private final List<RouteEntry> routeEntries = new ArrayList<>();

    public RouteEntryManager() {
    }

    public RouteEntryManager add(RouteEntry routeEntry) {
        routeEntries.add(routeEntry);
        return this;
    }

    public List<RouteEntry> getRouteEntries() {
        return routeEntries;
    }
}
