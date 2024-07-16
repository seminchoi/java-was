package codesquad.app.usecase;

import codesquad.app.model.Post;
import codesquad.app.model.User;
import codesquad.app.service.SessionService;
import codesquad.app.storage.PostDao;
import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.security.Session;
import codesquad.http.structure.Params;
import codesquad.template.DynamicHtml;

import java.util.List;

@Component
public class PostUsecase {
    private final SessionService sessionService;
    private final PostDao postDao;

    public PostUsecase(SessionService sessionService, PostDao postDao) {
        this.sessionService = sessionService;
        this.postDao = postDao;
    }

    public DynamicHtml getPostList(HttpRequest httpRequest) {
        Session session = sessionService.getSession(httpRequest);

        DynamicHtml dynamicHtml = new DynamicHtml();
        boolean authenticated = sessionService.isAuthenticated(session);
        dynamicHtml.setArg("authenticated", authenticated);
        if (authenticated) {
            User user = sessionService.getUser(session);
            dynamicHtml.setArg("user", user);
        }
        List<Post> posts = postDao.findAll();
        dynamicHtml.setArg("posts", posts);
        dynamicHtml.setTemplate("/index.html");
        return dynamicHtml;
    }

    public HttpResponse createPost(HttpRequest httpRequest) {
        Session session = sessionService.getSession(httpRequest);

        if (!sessionService.isAuthenticated(session)) {
            throw new HttpException(HttpStatus.UNAUTHORIZED, "로그인 후 이용 가능합니다.");
        }
        User user = sessionService.getUser(session);

        Params params = httpRequest.getBodyByUrlDecodedParams();

        Post post = new Post(
                params.get("title"),
                params.get("contnent"),
                user.getUserId()
        );

        postDao.save(post);

        return new HttpResponse(HttpStatus.CREATED);
    }
}
