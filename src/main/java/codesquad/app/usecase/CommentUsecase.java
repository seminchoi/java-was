package codesquad.app.usecase;

import codesquad.app.model.Comment;
import codesquad.app.model.User;
import codesquad.app.service.SessionService;
import codesquad.app.storage.CommentDao;
import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.security.Session;
import codesquad.http.structure.Params;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CommentUsecase {
    private static final Pattern COMMNET_CREATE_PATH = Pattern.compile("/post/(\\d+)/comment/create");
    private final CommentDao commendDao;
    private final SessionService sessionService;

    public CommentUsecase(CommentDao commendDao, SessionService sessionService) {
        this.commendDao = commendDao;
        this.sessionService = sessionService;
    }

    public HttpResponse createComment(HttpRequest httpRequest) {
        Session session = sessionService.getSession(httpRequest);

        if(!sessionService.isAuthenticated(session)) {
            throw new HttpException(HttpStatus.UNAUTHORIZED);
        }

        User user = sessionService.getUser(session);

        Params params = httpRequest.getBodyByUrlDecodedParams();
        String content = params.get("content");

        Matcher matcher = COMMNET_CREATE_PATH.matcher(httpRequest.getPath());
        matcher.find();
        Long postId = Long.parseLong(matcher.group(1));

        Comment comment = new Comment(content, postId, user.getUserId());
        commendDao.save(comment);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.FOUND);
        httpResponse.writeHeader("Location", "/post/" + postId);
        return httpResponse;
    }
}
