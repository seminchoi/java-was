package codesquad.server;

import codesquad.exception.HttpException;
import codesquad.http.ContentType;
import codesquad.http.HttpResponse;
import codesquad.http.HttpStatus;
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
        StringBuilder pathBuilder = new StringBuilder();
        for (String defaultPath : staticFilePathManager.getFilePaths()) {
            try {
                pathBuilder.append(defaultPath).append(path);
                URL url = StaticFileProcessor.class.getResource(pathBuilder.toString());
                if(url == null) {
                    continue;
                }

                if(isDirectory(url, pathBuilder.toString())) {
                    if(!pathBuilder.toString().endsWith("/")) {
                        pathBuilder.append("/");
                    }
                    pathBuilder.append("index.html");
                    url = StaticFileProcessor.class.getResource(pathBuilder.toString());
                    if(url == null) {
                        continue;
                    }
                }

                logger.info(url.getFile());
                InputStream inputStream = StaticFileProcessor.class.getResourceAsStream(pathBuilder.toString());
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
        httpResponse.writeHeader("Content-Type", getContentType(pathBuilder.toString()).getDirective());
        httpResponse.writeBody(fileBytes);

        return httpResponse;
    }


    private boolean isDirectory(URL url, String path) {
        if (url.getProtocol().equals("jar")) {
            try {
                JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
                JarFile jar = jarConnection.getJarFile();
                if(path.startsWith("/")) {
                    path = path.substring(1);
                }
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
