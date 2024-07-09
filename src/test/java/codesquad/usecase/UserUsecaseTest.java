package codesquad.usecase;

import codesquad.server.http.HttpMethod;
import codesquad.server.http.HttpRequest;
import codesquad.server.http.HttpResponse;
import codesquad.storage.UserStorage;
import codesquad.util.HttpRequestUtil;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class UserUsecaseTest {
    private UserUsecase userUsecase;

    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserStorage();

        userUsecase = new UserUsecase(userStorage);
    }

    @Test
    void 회원가입이_성공하면_로그인페이지로_리다이렉트_지시한다() throws URISyntaxException {

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Length", "37");
        HttpRequest httpRequest = HttpRequestUtil.createHttpRequest(HttpMethod.POST, "/user/create", headers);
        httpRequest.writeBody("userId=semin&name=semin&password=1234");
        HttpResponse httpResponse = userUsecase.createUser(httpRequest);
        String response = new String(httpResponse.makeResponse());

        Assertions.assertThat(response).contains("HTTP/1.1 302 Found");
        Assertions.assertThat(response).contains("Location: /");
    }
}
