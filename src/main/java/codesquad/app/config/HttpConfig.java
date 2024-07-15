package codesquad.app.config;

import codesquad.app.usecase.UserUsecase;
import codesquad.container.Container;
import codesquad.container.ContainerHolder;
import codesquad.http.HttpMethod;
import codesquad.server.router.RouteEntry;
import codesquad.server.router.RouteEntryConfigurer;

public class HttpConfig {
    public void config() {
        final Container container = ContainerHolder.getContainer();

        routeEntryConfig(container);
    }

    private void routeEntryConfig(final Container container) {
        RouteEntryConfigurer routeEntryConfigurer = (RouteEntryConfigurer) container.getComponent("routeEntryConfigurer");

        UserUsecase userUsecase = (UserUsecase) ContainerHolder.getContainer().getComponent("userUsecase");

        routeEntryConfigurer.add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/")
                        .handler(userUsecase::home)
                        .build()
        ).add(
                new RouteEntry.Builder().route(HttpMethod.POST, "/user/create")
                        .handler(userUsecase::register)
                        .build()
        ).add(
                new RouteEntry.Builder().route(HttpMethod.POST, "/login")
                        .handler(userUsecase::login)
                        .build()
        ).add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/logout")
                        .handler(userUsecase::logout)
                        .build()
        ).add(
                new RouteEntry.Builder().route(HttpMethod.GET, "/user/list")
                        .handler(userUsecase::userList)
                        .build()
        );
    }
}
