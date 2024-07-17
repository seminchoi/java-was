package codesquad.server.handler;

import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import codesquad.template.DynamicHtml;

@Component
public class ErrorHandler {
    private static final String ERROR_PATH = "/error/error.html";

    public HttpResponse handle(Exception exception) {
        HttpException httpException;
        if (exception instanceof HttpException) {
            httpException = (HttpException) exception;
        } else {
            httpException = new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러 입니다.");
        }
        ExceptionDto exceptionDto = new ExceptionDto(httpException);

        DynamicHtml dynamicHtml = new DynamicHtml();
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
