package codesquad.server.handler;

import codesquad.container.Component;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

@Component
public class CommonFileHandler {
    private final CommonFilePathManager commonFilePathManager;

    public CommonFileHandler(CommonFilePathManager commonFilePathManager) {
        this.commonFilePathManager = commonFilePathManager;
    }

    public HttpResponse handle(HttpRequest httpRequest) {
        String path = httpRequest.getPath();

        List<String> filePaths = commonFilePathManager.getFilePaths();

        for (String filePath : filePaths) {
            File file = new File(filePath + path);
            if (file.exists()) {
                try(FileInputStream fileInputStream = new FileInputStream(file)) {
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    int nRead;
                    byte[] data = new byte[1024];
                    while ((nRead = fileInputStream.read(data, 0, data.length)) != -1) {
                        buffer.write(data, 0, nRead);
                    }

                    buffer.flush();
                    HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
                    httpResponse.writeBody(buffer.toByteArray());
                    return httpResponse;
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
