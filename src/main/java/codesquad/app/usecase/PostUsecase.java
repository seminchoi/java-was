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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PostUsecase {
    private final Pattern POST_URL_PATTERN = Pattern.compile("/post/(\\d+)");

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
                params.get("content"),
                user.getUserId()
        );

        postDao.save(post);
        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/");
        return httpResponse;
    }

    public DynamicHtml getPostDetail(HttpRequest httpRequest) {
        String path = httpRequest.getPath();
        Matcher matcher = POST_URL_PATTERN.matcher(path);
        matcher.find();
        String group = matcher.group(1);

        Post post = postDao.findById(Long.parseLong(group)).orElseThrow(() ->
                new HttpException(HttpStatus.NOT_FOUND)
        );
        DynamicHtml dynamicHtml = new DynamicHtml();
        dynamicHtml.setTemplate("/post/post_detail.html");
        dynamicHtml.setArg("post", post);

        return dynamicHtml;
    }
}
