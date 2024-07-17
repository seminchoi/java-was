package codesquad.server.handler;

import codesquad.http.ContentType;
import codesquad.http.HttpResponse;
import codesquad.server.router.StaticFilePathManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class StaticFileHandlerTest {
    private StaticFileHandler staticFileHandler;

    @BeforeEach
    public void setUp() {
        StaticFilePathManager staticFilePathManager = new StaticFilePathManager();
        staticFilePathManager.addPath("static");
        staticFileHandler = new StaticFileHandler(staticFilePathManager);
    }

    @Test
    void 경로에_dot이_여러개_있어도_확장자를_확인할_수_있다() {
        HttpResponse httpResponse = staticFileHandler.handle("/hello.hi.html");

        assertThat(new String(httpResponse.makeResponse())).contains("Content-Type: text/html");
    }

    @Test
    void 파일의_확장자가_없으면_octet_stream_타입으로_반환한다() {
        HttpResponse httpResponse = staticFileHandler.handle("/hello");

        assertThat(new String(httpResponse.makeResponse())).contains(ContentType.APPLICATION_OCTET_STREAM.makeHeaderLine());
    }

    @Test
    void 경로가_파일이면_내용을_읽어서_response를_생성한다() {
        HttpResponse httpResponse = staticFileHandler.handle("/hello.hi.html");

        assertThat(new String(httpResponse.makeResponse())).contains("<!DOCTYPE html>");
    }

    @Test
    void 경로가_디렉토리면_index가_있는지_확인하고_반환한다() {
        HttpResponse httpResponse = staticFileHandler.handle("/login");

        assertThat(new String(httpResponse.makeResponse())).contains("<!DOCTYPE html>");
    }
}
