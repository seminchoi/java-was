package codesquad.app.service;

import codesquad.http.structure.MultiPartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class AppFileWriter {
    private static final Logger logger = LoggerFactory.getLogger(AppFileWriter.class);
    private static final String homePath = System.getProperty("user.home");
    private static final File UPLOAD_DIR;

    static {
        File wasDir = new File(homePath, "java-was");
        if (!wasDir.exists()) {
            wasDir.mkdirs();
        }

        final String UPLOAD_DIR_PATH = "img";
        UPLOAD_DIR = new File(wasDir, UPLOAD_DIR_PATH);

        if (!UPLOAD_DIR.exists()) {
            UPLOAD_DIR.mkdirs();
        }
    }

    public String saveFile(MultiPartFile file) {
        String originalFilename = file.getFileName();

        String uuid = UUID.randomUUID().toString();

        String fileName = uuid + originalFilename;

        File destFile = new File(UPLOAD_DIR, fileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(destFile)) {
            fileOutputStream.write(file.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return fileName;
    }
}
