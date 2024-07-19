package codesquad.app.usecase;

import codesquad.app.model.Comment;
import codesquad.app.model.Post;
import codesquad.app.storage.CommentDao;
import codesquad.app.storage.RdbmsPostDao;
import codesquad.container.Container;
import codesquad.container.ContainerConfigurer;
import codesquad.exception.HttpException;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.util.ContainerUtils;
import codesquad.util.LoginUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static codesquad.util.HttpRequestUtil.createHttpRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class CommentUsecaseTest extends TestWithTestDatabase {
    private static CommentUsecase commentUsecase;

    @BeforeAll
    public static void setUp() {
        ContainerConfigurer containerConfigurer = ContainerUtils.createContainerConfigurer(
                "codesquad.app.usecase.PostUsecase",
                "codesquad.app.usecase.CommentUsecase",
                "codesquad.app.usecase.UserUsecase",
                "codesquad.app.service.TemplateHeaderService",
                "codesquad.app.storage.RdbmsPostDao",
                "codesquad.app.storage.RdbmsUserDao",
                "codesquad.app.storage.RdbmsCommentDao",
                "codesquad.http.security.SessionStorage",
                "codesquad.db.TestDataSourceConfigurer",
                "codesquad.db.TestH2ConnectionPoolManager",
                "codesquad.app.service.SessionService"
        );

        container = new Container(containerConfigurer);
        container.init();
        commentUsecase = (CommentUsecase) container.getComponent("commentUsecase");
    }

    @Test
    void 로그인_상태에서_댓글을_작성할_수_있다() throws URISyntaxException {
        Long commentId = createPostAndComment();

        CommentDao commentDao = (CommentDao) container.getComponent("rdbmsCommentDao");
        Comment comment = commentDao.findById(commentId).get();

        assertThat(comment.getId()).isEqualTo(commentId);
        assertThat(comment.getContent()).isEqualTo("comment");
    }

    @Test
    void 게시글이_없으면_HttpException_예외가_발생한다() throws URISyntaxException {
        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);

        HttpRequest commentCreateRequest = createHttpRequest(
                HttpMethod.POST, "/post/1/comment/create",
                sessionHeader, "content=comment");
        CommentUsecase commentUsecase = (CommentUsecase) container.getComponent("commentUsecase");


        assertThatThrownBy(() -> commentUsecase.createComment(commentCreateRequest))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void 로그인하지_않으면_댓글을_작성할_때_예외가_발생한다() throws URISyntaxException {
        createPost();

        HttpRequest commentCreateRequest = createHttpRequest(
                HttpMethod.POST, "/post/1/comment/create",
                new HashMap<>(), "content=comment");
        CommentUsecase commentUsecase = (CommentUsecase) container.getComponent("commentUsecase");


        assertThatThrownBy(() -> commentUsecase.createComment(commentCreateRequest))
                .isInstanceOf(HttpException.class);
    }

    private Long createPost() throws URISyntaxException {
        PostUsecase postUsecase = (PostUsecase) container.getComponent("postUsecase");

        Map<String, String> sessionHeader = LoginUtils.registerAndLogin(container);
        sessionHeader.put("Content-Type", "application/x-www-form-urlencoded");

        HttpRequest postCreateRequest = createHttpRequest(HttpMethod.POST, "/post/create",
                sessionHeader, "title=hi&content=hello");
        postUsecase.createPost(postCreateRequest);

        RdbmsPostDao rdbmsPostDao = (RdbmsPostDao) container.getComponent("rdbmsPostDao");
        Post post = rdbmsPostDao.findAll().get(0);
        return post.getId();
    }

    private Long createPostAndComment() throws URISyntaxException {
        PostUsecase postUsecase = (PostUsecase) container.getComponent("postUsecase");

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

        CommentDao commentDao = (CommentDao) container.getComponent("rdbmsCommentDao");
        return commentDao.findAll().get(0).getId();
    }
}
