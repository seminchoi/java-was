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

    private final String UPLOAD_DIR = "img";

    public String saveFile(MultiPartFile file) {
        String projectRoot = System.getProperty("user.home");

        File uploadDir = new File(projectRoot, UPLOAD_DIR);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        String originalFilename = file.getFileName();

        String uuid = UUID.randomUUID().toString();

        String newFilename = uuid + originalFilename;

        File destFile = new File(uploadDir, newFilename);

        try (FileOutputStream fileOutputStream = new FileOutputStream(destFile)) {
            fileOutputStream.write(file.getContent());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return uploadDir.getAbsolutePath() + File.separator + newFilename;
    }
}
