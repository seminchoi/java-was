package codesquad.app.usecase;

import codesquad.app.model.User;
import codesquad.app.storage.UserStorage;
import codesquad.exception.HttpException;
import codesquad.http.HttpMethod;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import codesquad.http.security.SessionStorage;
import codesquad.util.HttpRequestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static codesquad.util.HttpRequestUtil.createHttpRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class UserUsecaseTest {
    private UserUsecase userUsecase;
    private UserStorage userStorage;
    private SessionStorage sessionStorage;


    @BeforeEach
    void setUp() {
        userStorage = new UserStorage();
        sessionStorage = new SessionStorage();
        userUsecase = new UserUsecase(userStorage, sessionStorage);
    }

    @Test
    void 홈_화면을_요청할때_로그인_되지_않았다면_로그인_버튼이_존재한다() throws URISyntaxException {
        HttpRequest httpRequest = createHttpRequest("/");
        HttpResponse httpResponse = userUsecase.home(httpRequest);

        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("로그인");
        assertThat(response).doesNotContain("로그아웃");
    }

    @Test
    void 홈_화면을_요청할때_로그인_되었다면_사용자_이름과_로그아웃_버튼이_존재한다() throws URISyntaxException {
        register();
        HttpResponse loginResponse = login("semin", "1234");
        String sessionId = getSessionId(loginResponse);

        HttpRequest httpRequest = createHttpRequest(HttpMethod.GET, "/", createHeaders(sessionId));
        HttpResponse homeResponse = userUsecase.home(httpRequest);

        String response = new String(homeResponse.makeResponse());

        assertThat(response).contains("로그아웃");
        assertThat(response).contains("semin");
        assertThat(response).doesNotContain("로그인");
    }

    @Test
    void 회원가입이_성공하면_로그인페이지로_리다이렉트_지시한다() throws URISyntaxException {
        String response = new String(register().makeResponse());

        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /");
    }

    @Test
    void 로그인이_성공하면_홈페이지로_리다이렉트_지시한다() throws URISyntaxException {
        register();
        HttpResponse httpResponse = login("semin", "1234");
        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /");
    }

    @Test
    void 아이디가_잘못되면_login_failed페이지로_리다이렉트한다() throws URISyntaxException {
        register();
        HttpResponse httpResponse = login("seminn", "1234");

        String response = new String(httpResponse.makeResponse());
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /login/login_failed.html");
    }

    @Test
    void 패스워드가_잘못되면_login_failed페이지로_리다이렉트한다() throws URISyntaxException {
        register();
        HttpResponse httpResponse = login("semin", "12345");

        String response = new String(httpResponse.makeResponse());
        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /login/login_failed.html");
    }

    @Test
    void 로그인_성공시_세션_저장소에_세션이_저장된다() throws URISyntaxException {
        register();
        HttpResponse httpResponse = login("semin", "1234");
        String sessionId = getSessionId(httpResponse);

        assertThat(sessionStorage.find(sessionId)).isNotEmpty();
    }

    @Test
    void 로그아웃_요청시_세션이_없으면_예외를_반환한다() {
        assertThatThrownBy(() -> logout(null))
                .isInstanceOf(HttpException.class);
    }

    @Test
    void 로그아웃_요청_시_세션이_삭제된다() throws URISyntaxException {
        register();
        HttpResponse httpResponse = login("semin", "1234");
        String sessionId = getSessionId(httpResponse);
        logout(sessionId);

        assertThat(sessionStorage.find(sessionId)).isEmpty();
    }

    @Test
    void 로그아웃_요청_시_홈페이지로_리다이렉트_한다() throws URISyntaxException {
        register();
        HttpResponse loginResponse = login("semin", "1234");
        String sessionId = getSessionId(loginResponse);
        HttpResponse logoutResponse = logout(sessionId);
        String response = new String(logoutResponse.makeResponse());

        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /");
    }

    @Test
    void 사용자_리스트_페이지_요청_시_로그인_되어있지_않으면_로그인페이지로_리다이렉트_된다() throws URISyntaxException {
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest("/user/list");
        HttpResponse httpResponse = userUsecase.userList(httpRequest);

        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("HTTP/1.1 302 Found");
        assertThat(response).contains("Location: /login");
    }

    @Test
    void 사용자_리스트_페이지_요청_시_로그인_되어있으면_회원가입_사용자_목록을_반환한다() throws URISyntaxException {
        userStorage.saveUser(new User("semin1","semin1","semin1"));
        userStorage.saveUser(new User("semin2","semin2","semin2"));
        userStorage.saveUser(new User("semin3","semin3","semin3"));
        HttpResponse loginResponse = login("semin1", "semin1");
        String sessionId = getSessionId(loginResponse);
        Map<String, String> headers = createHeaders(sessionId);
        HttpResponse httpResponse = userUsecase.userList(
                HttpRequestUtil.createHttpRequest(HttpMethod.GET, "/user/list", headers)
        );

        String response = new String(httpResponse.makeResponse());

        assertThat(response).contains("semin1");
        assertThat(response).contains("semin2");
        assertThat(response).contains("semin3");
    }

    private HttpResponse register() throws URISyntaxException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "37");
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/user/create", headers);
        httpRequest.writeBody("userId=semin&name=semin&password=1234");
        return userUsecase.register(httpRequest);
    }

    private HttpResponse login(String id, String password) throws URISyntaxException {
        Map<String, String> headers = new HashMap<>();
        String body = "userId=" + id+ "&password=" + password;
        headers.put("Content-Length", String.valueOf(body.length()));
        HttpRequest httpRequest = createHttpRequest(HttpMethod.POST, "/login", headers);
        httpRequest.writeBody(body);
        return userUsecase.login(httpRequest);
    }

    private String getSessionId(HttpResponse httpResponse) {
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

    private HttpResponse logout(String sessionId) throws URISyntaxException {
        Map<String, String> headers = createHeaders(sessionId);
        HttpRequest httpRequest = createHttpRequest(HttpMethod.GET, "/logout", headers);
        return userUsecase.logout(httpRequest);
    }

    private Map<String, String> createHeaders(String sessionId) {
        Map<String, String> headers = new HashMap<>();
        if(sessionId != null) {
            headers.put("Cookie", "SID="+sessionId);
        }
        return headers;
    }
}
