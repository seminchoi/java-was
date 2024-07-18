package codesquad.http.structure;

import codesquad.exception.HttpException;
import codesquad.http.ContentType;
import codesquad.http.HttpStatus;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Params {
    private final Map<String, Object> params = new HashMap<>();

    public Params(String body) {
        parseUrlEncoded(body);
    }

    public Params(byte[] body, ContentType contentType, String boundary) {
        if (contentType == ContentType.APPLICATION_X_WWW_FORM_URLENCODED) {
            parseUrlEncoded(new String(body));
        }

        if (contentType == ContentType.MULTIPART_FORM_DATA) {
            try {
                parseMultipartData(body, boundary);
            } catch (IOException e) {
                throw new HttpException(HttpStatus.INTERNAL_SERVER_ERROR, "요청을 처리하는 중 오류가 발생했습니다.");
            }
        }
    }

    private void parseUrlEncoded(String body) {
        if (body == null) {
            return;
        }
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            int splitIndex = pair.indexOf("=");
            if (splitIndex == -1) {
                continue;
            }

            String key = decodeUrlComponent(pair.substring(0, splitIndex));
            String value = null;
            if (pair.length() > splitIndex + 1) {
                value = decodeUrlComponent(pair.substring(splitIndex + 1));
            }

            if (value != null && !params.containsKey(key)) {
                params.put(key, value);
            }
        }
    }

    public void parseMultipartData(byte[] body, String boundary) throws IOException {
        byte[] boundaryBytes = ("--" + boundary).getBytes();
        byte[] endBoundaryBytes = ("--" + boundary + "--").getBytes();
        int startIndex = 0;

        while (startIndex < body.length) {
            int boundaryIndex = findBoundary(body, boundaryBytes, startIndex);
            if (boundaryIndex == -1) {
                break;
            }

            if (startIndex != boundaryIndex) {
                processPart(body, startIndex, boundaryIndex);
            }

            if (isEndBoundary(body, boundaryIndex, endBoundaryBytes)) {
                break;
            }

            startIndex = boundaryIndex + boundaryBytes.length;
        }
    }

    private int findBoundary(byte[] body, byte[] boundaryBytes, int startIndex) {
        for (int i = startIndex; i <= body.length - boundaryBytes.length; i++) {
            if (isFullBoundary(body, i, boundaryBytes)) {
                return i;
            }
        }
        return -1;
    }

    private boolean isFullBoundary(byte[] body, int startIndex, byte[] boundaryBytes) {
        if (startIndex + boundaryBytes.length > body.length) {
            return false;
        }
        for (int i = 0; i < boundaryBytes.length; i++) {
            if (body[startIndex + i] != boundaryBytes[i]) {
                return false;
            }
        }
        return true;
    }

    private void processPart(byte[] body, int startIndex, int endIndex) throws IOException {
        ByteArrayOutputStream headerStream = new ByteArrayOutputStream();
        int i = startIndex;

        while (i < endIndex - 3) {
            if (isEndOfHeader(body, i)) {
                break;
            }
            headerStream.write(body[i++]);
        }

        Map<String, String> headers = parseHeaders(headerStream.toByteArray());
        Map<String, String> dispositionInfo = parseContentDisposition(headers.get("content-disposition"));
        String name = dispositionInfo.get("name");
        String filename = dispositionInfo.get("filename");

        i += 4;

        byte[] content = Arrays.copyOfRange(body, i, endIndex);

        if (filename != null) {
            ContentType contentType = ContentType.fromDirective(headers.getOrDefault("content-type", "application/octet-stream"));
            MultiPartFile file = new MultiPartFile(filename, contentType, content);
            params.put(name, file);
        } else {
            params.put(name, new String(content).trim());
        }
    }

    private boolean isEndOfHeader(byte[] body, int index) {
        return index + 3 < body.length &&
                body[index] == '\r' && body[index + 1] == '\n' &&
                body[index + 2] == '\r' && body[index + 3] == '\n';
    }


    private Map<String, String> parseHeaders(byte[] headerBytes) {
        Map<String, String> headers = new HashMap<>();
        String headerString = new String(headerBytes);
        String[] lines = headerString.split("\r\n");
        for (String line : lines) {
            String[] parts = line.split(": ", 2);
            if (parts.length == 2) {
                headers.put(parts[0].toLowerCase(), parts[1]);
            }
        }
        return headers;
    }

    private Map<String, String> parseContentDisposition(String contentDisposition) {
        Map<String, String> result = new HashMap<>();
        String[] parts = contentDisposition.split(";");
        for (String part : parts) {
            part = part.trim();
            String[] keyValue = part.split("=", 2);
            if (keyValue.length == 2) {
                String key = keyValue[0].trim();
                String value = keyValue[1].trim().replaceAll("\"", "");
                result.put(key, value);
            }
        }
        return result;
    }

    private boolean isEndBoundary(byte[] body, int index, byte[] endBoundaryBytes) {
        if (index + endBoundaryBytes.length > body.length) {
            return false;
        }
        for (int i = 0; i < endBoundaryBytes.length; i++) {
            if (body[index + i] != endBoundaryBytes[i]) {
                return false;
            }
        }
        return true;
    }

    private String decodeUrlComponent(String component) {
        try {
            return URLDecoder.decode(component, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public String get(String key) {
        return (String) params.get(key);
    }

    public MultiPartFile getFile(String key) {
        return (MultiPartFile) params.get(key);
    }
}
