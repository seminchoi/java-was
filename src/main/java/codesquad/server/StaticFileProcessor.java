package codesquad.server;

import codesquad.exception.HttpException;
import codesquad.http.ContentType;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StaticFileProcessor {
    private final static Logger logger = LoggerFactory.getLogger(StaticFileProcessor.class);
    private final StaticFilePathManager staticFilePathManager;

    public StaticFileProcessor(StaticFilePathManager staticFilePathManager) {
        this.staticFilePathManager = staticFilePathManager;
    }

    public HttpResponse readFile(String path) {
        byte[] fileBytes = null;

        for (String defaultPath : staticFilePathManager.getFilePaths()) {
            try {
                InputStream inputStream = StaticFileProcessor.class.getResourceAsStream(defaultPath + path);
                if (inputStream != null) {
                    fileBytes = getBytes(inputStream);
                }
            } catch (IOException e) {
                throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        if (fileBytes == null) {
            return null;
        }

        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        httpResponse.writeHeader("Content-Type", getContentType(path).getDirective());
        httpResponse.writeBody(fileBytes);

        return httpResponse;
    }

    public ContentType getContentType(String path) {
        int extensionIndex = path.lastIndexOf(".");
        if (extensionIndex < 0) {
            return ContentType.APPLICATION_OCTET_STREAM;
        }
        String extension = path.substring(extensionIndex + 1);
        return ContentType.fromFileExtension(extension);
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }
}
