package codesquad.server.handler;

import codesquad.app.model.User;
import codesquad.app.service.SessionService;
import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.http.security.Session;
import codesquad.template.DynamicHtml;

@Component
public class ErrorHandler {
    private static final String ERROR_PATH = "/error/error.html";
    private final SessionService sessionService;

    public ErrorHandler(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    public HttpResponse handle(HttpRequest httpRequest, Exception exception) {
        HttpException httpException;
        if (exception instanceof HttpException) {
            httpException = (HttpException) exception;
        } else {
            httpException = new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러 입니다.");
        }

        DynamicHtml dynamicHtml = new DynamicHtml();

        Session session = sessionService.getSession(httpRequest);
        if(sessionService.isAuthenticated(session)) {
            User user = sessionService.getUser(session);
            dynamicHtml.setArg("authenticated", true);
            dynamicHtml.setArg("user", user);
        }

        ExceptionDto exceptionDto = new ExceptionDto(httpException);

        dynamicHtml.setTemplate(ERROR_PATH);
        dynamicHtml.setArg("exception", exceptionDto);

        return dynamicHtml.process(httpException.getHttpStatus());
    }

    private static class ExceptionDto {
        private String code;
        private String message;

        public ExceptionDto(HttpException httpException) {
            this.code = httpException.getHttpStatus().getStatus();
            System.out.println(httpException.getMessage());
            this.message = httpException.getMessage();
        }
    }
}
