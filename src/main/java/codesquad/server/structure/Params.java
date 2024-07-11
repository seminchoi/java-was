package codesquad.server.structure;

import java.util.HashMap;
import java.util.Map;

public class Params {
    private final Map<String, String> params = new HashMap<>();

    public Params(String query) {
        if (query == null) {
            return;
        }
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int splitIndex = pair.indexOf("=");
            if (splitIndex == -1) {
                continue;
            }

            String key = pair.substring(0, splitIndex);
            String value = null;
            if (pair.length() > splitIndex + 1) {
                value = pair.substring(splitIndex + 1);
            }

            if (value != null && !params.containsKey(key)) {
                params.put(key, value);
            }
        }
    }

    public String get(String key) {
        return params.get(key);
    }
}
