package codesquad.http;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.stream.Collectors;

public class ResourceFile {
    private final File file;

    public ResourceFile(File file) {
        this.file = file;
    }
    
    public ContentType getContentType() {
        String name = file.getName();
        int extensionIndex = name.lastIndexOf(".");
        if(extensionIndex < 0) {
            //TODO : 처리 방안 생각
            return ContentType.TEXT_PLAIN;
        }
        String extension = name.substring(extensionIndex);
        return ContentType.fromFileExtension(extension);
    }

    public byte[] getBytes() throws FileNotFoundException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        return bufferedReader.lines().collect(Collectors.joining()).getBytes();
    }
}
