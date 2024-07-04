package codesquad.domain.user;

import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

public class User {
    private String userId;
    private String password;
    private String name;

    public User(String userId, String password, String name) {
        //TODO: 도메인객체가 Http 예외나 상태코드를 알 필요는 없다
        if (userId == null || userId.isEmpty()
                || password == null || password.isEmpty()
                || name == null || name.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
