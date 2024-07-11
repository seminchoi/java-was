package codesquad.server.router;

import codesquad.exception.HttpException;
import codesquad.server.file.AppFileReader;
import codesquad.server.http.ContentType;
import codesquad.server.http.HttpResponse;
import codesquad.server.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class StaticFileProcessor {
    private final static Logger logger = LoggerFactory.getLogger(StaticFileProcessor.class);
    private final StaticFilePathManager staticFilePathManager;

    public StaticFileProcessor(StaticFilePathManager staticFilePathManager) {
        this.staticFilePathManager = staticFilePathManager;
    }

    public HttpResponse readFile(String path) {
        AppFileReader fileReader = null;
        String fullPath;
        for (String pathPrefix : staticFilePathManager.getFilePaths()) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            fullPath = makeFullPath(pathPrefix, path);
            URL url = classLoader.getResource(fullPath);
            if (url == null) {
                continue;
            }

            if (isDirectory(url, fullPath)) {
                fullPath = fullPath + "/index.html";
                url = classLoader.getResource(fullPath);
                if (url == null) {
                    continue;
                }
            }

            logger.info("found file path {}", fullPath);
            fileReader = new AppFileReader(fullPath);
            break;
        }

        if (fileReader == null) {
            return null;
        }

        HttpResponse httpResponse = new HttpResponse(HttpStatus.OK);
        httpResponse.writeHeader("Content-Type", fileReader.getContentType().getDirective());
        httpResponse.writeBody(fileReader.getBytes());

        return httpResponse;
    }

    private String makeFullPath(String pathPrefix, String path) {
        if (path.equals("/")) {
            return pathPrefix;
        }
        return pathPrefix + path;
    }


    private boolean isDirectory(URL url, String path) {
        if (url.getProtocol().equals("jar")) {
            try {
                JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                JarFile jar = jarConnection.getJarFile();
                JarEntry entry = jar.getJarEntry(path);
                return entry.isDirectory();
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return new File(url.getFile()).isDirectory();
    }
}
