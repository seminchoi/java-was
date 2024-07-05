package codesquad.usecase;

import codesquad.storage.UserStorage;
import codesquad.http.HttpRequest;
import codesquad.http.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class UserUsecaseTest {
    private UserUsecase userUsecase;

    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new UserStorage();

        userUsecase = new UserUsecase(userStorage);
    }

    @Test
    void 회원가입이_성공하면_로그인페이지로_리다이렉트_지시한다() {
        HttpRequest httpRequest = new HttpRequest(List.of(
                "GET /create/user?userId=semin&name=semin&password=1234 HTTP/1.1"
        ));
        HttpResponse httpResponse = userUsecase.createUser(httpRequest);
        String response = new String(httpResponse.makeResponse());

        Assertions.assertThat(response).contains("HTTP/1.1 303 See Other");
        Assertions.assertThat(response).contains("Location: /login/index.html");
    }
}
