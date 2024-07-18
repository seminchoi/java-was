package codesquad.file;

import codesquad.exception.HttpException;
import codesquad.http.ContentType;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AppFileReader {
    private final static Logger logger = LoggerFactory.getLogger(AppFileReader.class);

    private String path;

    public AppFileReader(String path) {
        this.path = path;
    }

    public ContentType getContentType() {
        int extensionIndex = path.lastIndexOf(".");
        if (extensionIndex < 0) {
            return ContentType.APPLICATION_OCTET_STREAM;
        }
        String extension = path.substring(extensionIndex + 1);
        return ContentType.fromFileExtension(extension);
    }

    public byte[] getBytes() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(path)) {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public String getContent() {
        return new String(getBytes());
    }
}

