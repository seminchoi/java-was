package codesquad.http;

import codesquad.exception.HttpException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Deprecated
public class FileReader {
    private static final Logger logger = LoggerFactory.getLogger(FileReader.class);
    private final File file;

    public FileReader(File file) {
        this.file = file;
    }

    public ContentType getContentType() {
        String name = file.getName();
        int extensionIndex = name.lastIndexOf(".");
        if (extensionIndex < 0) {
            return ContentType.APPLICATION_OCTET_STREAM;
        }
        String extension = name.substring(extensionIndex + 1);
        return ContentType.fromFileExtension(extension);
    }

    public byte[] getBytes() {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] fileBytes = new byte[(int) file.length()];

            fileInputStream.read(fileBytes);

            return fileBytes;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
