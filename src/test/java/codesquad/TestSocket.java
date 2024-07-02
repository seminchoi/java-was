package codesquad;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class TestSocket extends Socket {
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private boolean processed = false;

    public TestSocket() {
        outputStream = new ByteArrayOutputStream();
        inputStream = new ByteArrayInputStream(new byte[0]);
    }

    @Override
    public OutputStream getOutputStream() {
        return outputStream;
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void close() {
        processed = true;
    }

    public void setInputStreamContent(String content) {
        inputStream = new ByteArrayInputStream(content.getBytes());
    }

    public String getOutputStreamContent() {
        return outputStream.toString();
    }

    public boolean wasProcessed() {
        return processed;
    }
}
