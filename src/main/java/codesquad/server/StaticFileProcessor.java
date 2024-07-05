package codesquad.server;

import codesquad.http.FileReader;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class StaticFileProcessor {
    private final static Logger logger = LoggerFactory.getLogger(StaticFileProcessor.class);
    private final StaticFilePathManager staticFilePathManager;

    public StaticFileProcessor(StaticFilePathManager staticFilePathManager) {
        this.staticFilePathManager = staticFilePathManager;
    }

    public HttpResponse readFile(String path) {
        File file = null;

        for (String defaultPath : staticFilePathManager.getFilePaths()) {
            logger.info("Reading file {}", defaultPath + path);
            String filePath = StaticFileProcessor.class.getResource(defaultPath + path).getFile();
            File curFile = new File(filePath);
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
