package codesquad.model;

import codesquad.exception.HttpException;
import codesquad.server.http.HttpStatus;

import java.util.Objects;

public class User {
    private String userId;
    private String password;
    private String name;

    public User(String userId, String password, String name) {
        //TODO: 도메인객체가 Http 예외나 상태코드를 알 필요는 없다
        if (userId == null || userId.isBlank()
                || password == null || password.isBlank()
                || name == null || name.isBlank()) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
