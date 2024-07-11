package codesquad.http;

import codesquad.exception.HttpException;

import java.util.HashMap;
import java.util.Map;

public class URI {
    private String path;
    private final Map<String, String> params = new HashMap<>();

    public URI(String path) {
        String[] splitUri = path.split("\\?");
        this.path = splitUri[0];

        if (splitUri.length > 1) {
            parseQueryStrings(splitUri[1]);
        }
    }
    
    private void parseQueryStrings(String requestQueryString) {
        String[] queryStrings = requestQueryString.split("&");
        for (String queryString : queryStrings) {
            String[] keyValue = queryString.split("=");
            if(keyValue.length != 2) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            this.params.put(keyValue[0], keyValue[1]);
        }
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
