package codesquad.http;

import codesquad.exception.HttpException;

import java.util.HashMap;
import java.util.Map;

public class Params {
    private final Map<String, String> params = new HashMap<>();

    public Params(String query) {
        if(query == null) {
            return;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] splitPair = pair.split("=");
            if (splitPair.length != 2) {
                throw new HttpException(HttpStatus.BAD_REQUEST);
            }
            if (params.containsKey(splitPair[0])) {
                params.put(splitPair[0], params.get(splitPair[0]) + "," + splitPair[1]);
                continue;
            }
            params.put(splitPair[0], splitPair[1]);
        }
    }

    public String get(String key) {
        return params.get(key);
    }
}
