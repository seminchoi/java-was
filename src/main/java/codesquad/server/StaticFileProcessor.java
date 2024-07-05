package codesquad.server;

import codesquad.http.FileReader;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;

import java.io.File;

public class StaticFileProcessor {
    private final StaticFilePathManager staticFilePathManager;

    public StaticFileProcessor(StaticFilePathManager staticFilePathManager) {
        this.staticFilePathManager = staticFilePathManager;
    }

    public HttpResponse readFile(String path) {
        File file = null;

        for (String defaultPath : staticFilePathManager.getFilePaths()) {
            String absolutePath = System.getProperty("user.dir") + File.separator + defaultPath + path;

            File curFile = new File(absolutePath);

            if (curFile.exists()) {
                file = curFile;
                break;
            }
        }
        if (file == null) {
            return null;
        }

        FileReader fileReader = new FileReader(file);

        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        httpResponse.writeHeader("Content-Type", fileReader.getContentType().getDirective());
        httpResponse.writeBody(fileReader.getBytes());

        return httpResponse;
    }
}
