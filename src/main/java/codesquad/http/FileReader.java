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
            //TODO : 처리 방안 생각
            return ContentType.TEXT_PLAIN;
        }
        String extension = name.substring(extensionIndex);
        return ContentType.fromFileExtension(extension);
    }

    public byte[] getBytes() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new java.io.FileReader(file));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString().getBytes();
    }
}
