package codesquad.app.usecase;

import codesquad.app.model.Post;
import codesquad.app.storage.RdbmsPostDao;
import codesquad.container.Container;
import codesquad.container.ContainerConfigurer;
import codesquad.exception.HttpException;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.util.ContainerUtils;
import codesquad.util.HttpRequestUtil;
import codesquad.util.LoginUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostUsecaseTest {
    private Container container;
    private PostUsecase postUsecase;

    @BeforeEach
    public void setUp() {
        ContainerConfigurer containerConfigurer = ContainerUtils.createContainerConfigurer(
                "codesquad.app.usecase.PostUsecase",
                "codesquad.app.usecase.UserUsecase",
                "codesquad.app.storage.RdbmsPostDao",
                "codesquad.app.storage.RdbmsUserDao",
                "codesquad.app.storage.RdbmsCommentDao",
                "codesquad.http.security.SessionStorage",
                "codesquad.db.TestDataSourceConfigurer",
                "codesquad.db.H2ConnectionPoolManager",
                "codesquad.app.service.SessionService"
        );

        container = new Container(containerConfigurer);
        container.init();
        postUsecase = (PostUsecase) container.getComponent("postUsecase");
    }

    @Test
    void 게시글을_작성할_때_로그인하지_않으면_예외가_발생한다() throws URISyntaxException {
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest(HttpMethod.POST, "/post/create");
        assertThatThrownBy(() -> postUsecase.createPost(httpRequest))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void 게시글을_작성할_때_로그인했으면_리다이렉트_된다() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        HttpRequest httpRequest = HttpRequestUtil
                .createHttpRequest(HttpMethod.POST, "/post/create", sessionHeader, "title=hi&content=hello");

        HttpResponse httpResponse = postUsecase.createPost(httpRequest);
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("Location: /");
    }

    @Test
    void 게시글을_작성하면_게시글이_저장된다() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        HttpRequest httpRequest = HttpRequestUtil
                .createHttpRequest(HttpMethod.POST, "/post/create", sessionHeader, "title=hi&content=hello");
        postUsecase.createPost(httpRequest);
        RdbmsPostDao rdbmsPostDao = (RdbmsPostDao) container.getComponent("rdbmsPostDao");

        List<Post> posts = rdbmsPostDao.findAll();

        assertThat(posts.size()).isEqualTo(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("hi");
        assertThat(posts.get(0).getContent()).isEqualTo("hello");
    }
}
