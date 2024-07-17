package codesquad.app.usecase;

import codesquad.app.model.Post;
import codesquad.app.storage.RdbmsPostDao;
import codesquad.container.Container;
import codesquad.container.ContainerConfigurer;
import codesquad.exception.HttpException;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.template.DynamicHtml;
import codesquad.util.ContainerUtils;
import codesquad.util.HttpRequestUtil;
import codesquad.util.LoginUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import static codesquad.util.HttpRequestUtil.createHttpRequest;
import static codesquad.util.LoginUtils.createHeaders;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PostUsecaseTest {
    private Container container;
    private PostUsecase postUsecase;

    @BeforeEach
    public void setUp() {
        ContainerConfigurer containerConfigurer = ContainerUtils.createContainerConfigurer(
                "codesquad.app.usecase.PostUsecase",
                "codesquad.app.usecase.CommentUsecase",
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
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/post/create");
        assertThatThrownBy(() -> postUsecase.createPost(httpRequest))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void 게시글을_작성할_때_로그인했으면_리다이렉트_된다() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        sessionHeader.put("Content-Type", "application/x-www-form-urlencoded");
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/post/create", sessionHeader, "title=hi&content=hello");

        HttpResponse httpResponse = postUsecase.createPost(httpRequest);
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("Location: /");
    }

    @Test
    void 게시글을_작성하면_게시글이_저장된다() throws URISyntaxException {
        createPost();
        RdbmsPostDao rdbmsPostDao = (RdbmsPostDao) container.getComponent("rdbmsPostDao");

        List<Post> posts = rdbmsPostDao.findAll();

        assertThat(posts.size()).isEqualTo(1);
        assertThat(posts.get(0).getTitle()).isEqualTo("hi");
        assertThat(posts.get(0).getContent()).isEqualTo("hello");
    }

    @Test
    void 메인_페이지에서_게시글_목록을_확인할_수_있다() throws URISyntaxException {
        createPost();
        HttpRequest httpRequest = createHttpRequest("/");
        DynamicHtml dynamicHtml = postUsecase.getPostList(httpRequest);
        HttpResponse httpResponse = dynamicHtml.process();
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("hi");
        assertThat(response).contains("semin");
    }

    @Test
    void 메인페이지에_접속할_때_로그인_되지_않았다면_로그인_버튼이_존재한다() throws URISyntaxException {
        HttpRequest httpRequest = createHttpRequest("/");
        DynamicHtml dynamicHtml = postUsecase.getPostList(httpRequest);
        HttpResponse httpResponse = dynamicHtml.process();

        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("로그인");
        assertThat(response).doesNotContain("로그아웃");
    }

    @Test
    void 홈_화면을_요청할때_로그인_되었다면_사용자_이름과_로그아웃_버튼이_존재한다() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        HttpRequest httpRequest = createHttpRequest(HttpMethod.GET, "/", sessionHeader);
        DynamicHtml dynamicHtml = postUsecase.getPostList(httpRequest);
        HttpResponse homeResponse = dynamicHtml.process();

        String response = new String(homeResponse.makeResponse());

        assertThat(response).contains("로그아웃");
        assertThat(response).contains("semin");
        assertThat(response).doesNotContain("로그인");
    }

    @Test
    void 게시글_상세_보기에서_글의_상세_내용을_확인할_수_있다() throws URISyntaxException {
        createPost();
        RdbmsPostDao rdbmsPostDao = (RdbmsPostDao) container.getComponent("rdbmsPostDao");
        Post post = rdbmsPostDao.findAll().get(0);

        HttpRequest httpRequest = createHttpRequest("/post/" + post.getId());
        DynamicHtml dynamicHtml = postUsecase.getPostDetail(httpRequest);
        HttpResponse httpResponse = dynamicHtml.process();
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("hi");
        assertThat(response).contains("hello");
        assertThat(response).contains("semin");
    }

    @Test
    void 게시글_상세_보기에서_댓글의_내용도_확인할_수_있다() throws URISyntaxException {
        Long postId = createPostAndComment();

        HttpRequest httpRequest = createHttpRequest("/post/" + postId);
        DynamicHtml dynamicHtml = postUsecase.getPostDetail(httpRequest);
        HttpResponse httpResponse = dynamicHtml.process();
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("hi");
        assertThat(response).contains("hello");
        assertThat(response).contains("semin");
        assertThat(response).contains("comment");
    }

    private void createPost() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        sessionHeader.put("Content-Type", "application/x-www-form-urlencoded");
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/post/create",
                sessionHeader, "title=hi&content=hello");
        postUsecase.createPost(httpRequest);
    }

    private Long createPostAndComment() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        sessionHeader.put("Content-Type", "application/x-www-form-urlencoded");
        HttpRequest postCreateRequest = createHttpRequest(HttpMethod.POST, "/post/create",
                sessionHeader, "title=hi&content=hello");
        postUsecase.createPost(postCreateRequest);
        RdbmsPostDao rdbmsPostDao = (RdbmsPostDao) container.getComponent("rdbmsPostDao");
        Post post = rdbmsPostDao.findAll().get(0);

        String createCommentUrl = "/post/" + post.getId() + "/comment/create";
        HttpRequest commentCreateRequest = createHttpRequest(
                HttpMethod.POST, createCommentUrl,
                sessionHeader, "content=comment");
        CommentUsecase commentUsecase = (CommentUsecase) container.getComponent("commentUsecase");
        commentUsecase.createComment(commentCreateRequest);

        return post.getId();
    }
}
