package codesquad.util;

import codesquad.app.usecase.UserUsecase;
import codesquad.container.Container;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codesquad.util.HttpRequestUtil.createHttpRequest;

public class LoginUtils {
    public static HttpResponse register(Container container) throws URISyntaxException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "37");
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/user/create", headers);
        httpRequest.writeBody("userId=semin&name=semin&password=1234");
        UserUsecase userUsecase = (UserUsecase) container.getComponent("userUsecase");
        return userUsecase.register(httpRequest);
    }

    public static Map<String, String> registerAndLogin(Container container) throws URISyntaxException {
        register(container);
        Map<String, String> headers = new HashMap<>();
        String body = "userId=semin&password=1234";
        headers.put("Content-Length", String.valueOf(body.length()));
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/login", headers);
        httpRequest.writeBody(body);
        UserUsecase userUsecase = (UserUsecase) container.getComponent("userUsecase");
        HttpResponse login = userUsecase.login(httpRequest);
        String sessionId = getSessionId(login);
        return createHeaders(sessionId);
    }

    public static HttpResponse login(Container container, String id, String password) throws URISyntaxException {
        Map<String, String> headers = new HashMap<>();
        String body = "userId=" + id+ "&password=" + password;
        headers.put("Content-Length", String.valueOf(body.length()));
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/login", headers);
        httpRequest.writeBody(body);
        UserUsecase userUsecase = (UserUsecase) container.getComponent("userUsecase");
        return userUsecase.login(httpRequest);
    }



    public static String getSessionId(HttpResponse httpResponse) {
        List<String> setCookies = httpResponse.getHeader("Set-Cookie");
        String sessionId = null;
        for (String setCookie : setCookies) {
            int directiveDelimiter = setCookie.indexOf(";");
            String cookie = setCookie.substring(0, directiveDelimiter);

            int keyValueDelimiter = cookie.indexOf("=");
            String key = cookie.substring(0, keyValueDelimiter);
            if(key.equals("SID")) {
                sessionId = cookie.substring(keyValueDelimiter+1);
                break;
            }
        }

        return sessionId;
    }

    public static Map<String, String> createHeaders(String sessionId) {
        Map<String, String> headers = new HashMap<>();
        if(sessionId != null) {
            headers.put("Cookie", "SID="+sessionId);
        }
        return headers;
    }
}
