package codesquad.app.config;

import codesquad.app.usecase.PostUsecase;
import codesquad.app.usecase.UserUsecase;
import codesquad.container.Container;
import codesquad.container.ContainerHolder;
import codesquad.http.HttpMethod;
import codesquad.server.router.RouteEntry;
import codesquad.server.router.RouteEntryManager;
import codesquad.server.router.StaticFilePathManager;

public class HttpConfig {
    public void config() {
        final Container container = ContainerHolder.getContainer();

        routeEntryConfig(container);
        staticFilePathConfig(container);
    }

    private void routeEntryConfig(final Container container) {
        RouteEntryManager routeEntryManager = (RouteEntryManager) container.getComponent("routeEntryManager");

        UserUsecase userUsecase = (UserUsecase) ContainerHolder.getContainer().getComponent("userUsecase");
        PostUsecase postUsecase = (PostUsecase) ContainerHolder.getContainer().getComponent("postUsecase");

        routeEntryManager.add(
                        new RouteEntry.Builder().route(HttpMethod.GET, "/")
                                .handler(postUsecase::getPostList)
                                .build()
                ).add(
                        new RouteEntry.Builder().route(HttpMethod.POST, "/post/create")
                                .handler(postUsecase::createPost)
                                .build()
                ).add(
                        new RouteEntry.Builder().route(HttpMethod.GET, "/post/{postId}")
                                .handler(postUsecase::getPostDetail)
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

    private void staticFilePathConfig(final Container container) {
        StaticFilePathManager staticFilePathManager = (StaticFilePathManager) container.getComponent("staticFilePathManager");

        staticFilePathManager.addPath("static");
    }
}
