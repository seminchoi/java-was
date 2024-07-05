package codesquad.http;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@Deprecated
public class FileReaderTest {
    @Test
    void 파일의_확장자를_확인할_수_있다() {
        File file = new File("src/test/resources/static/hello.hi.html");
        FileReader fileReader = new FileReader(file);

        assertThat(fileReader.getContentType()).isEqualTo(ContentType.TEXT_HTML);
    }

    @Test
    void 파일의_확장자가_없으면_octet_stream_타입으로_반환한다() {
        File file = new File("src/test/resources/static/hello");
        FileReader fileReader = new FileReader(file);

        assertThat(fileReader.getContentType()).isEqualTo(ContentType.APPLICATION_OCTET_STREAM);
    }

    @Test
    void 파일의_내용을_읽을_수_있다() {
        File file = new File("src/test/resources/static/hello.hi.html");
        FileReader fileReader = new FileReader(file);

        assertThat(fileReader.getBytes()).isEqualTo("<!DOCTYPE html>".getBytes());
    }
}
