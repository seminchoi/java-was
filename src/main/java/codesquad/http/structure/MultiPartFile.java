package codesquad.http.structure;

import codesquad.http.ContentType;

public class MultiPartFile {
    private final String fileName;
    private final ContentType contentType;
    private final byte[] content;

    public MultiPartFile(String fileName, ContentType contentType, byte[] content) {
        this.fileName = fileName;
        this.contentType = contentType;
        this.content = content;
    }

    public String getFileName() {
        return fileName;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public byte[] getContent() {
        return content;
    }
}
