package codesquad.http;

import java.io.*;

public class FileReader {
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
        try(BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(file))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString().getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
