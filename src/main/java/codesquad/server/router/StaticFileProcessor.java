package codesquad.server.router;

import codesquad.exception.HttpException;
import codesquad.server.http.ContentType;
import codesquad.server.http.HttpResponse;
import codesquad.server.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
        byte[] fileBytes = null;
        String fullPath = path;
        for (String pathPrefix : staticFilePathManager.getFilePaths()) {
            try {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                //TODO: 요청이 /가 아닌데 /로 끝날 때는 /를 땐 곳으로 리다이렉트 시킨다.
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
                InputStream inputStream = classLoader.getResourceAsStream(fullPath);
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
        httpResponse.writeHeader("Content-Type", getContentType(fullPath).getDirective());
        httpResponse.writeBody(fileBytes);

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
