package codesquad.app.model;

import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

import java.util.Objects;

public class User {
    private String userId;
    private String password;
    private String name;

    public User(String userId, String password, String name) {
        validateUserId(userId);
        validatePassword(password);
        validateName(name);

        this.userId = userId;
        this.password = password;
        this.name = name;
    }

    private void validateUserId(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST,"사용자 ID를 입력하지 않았습니다.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST,"비밀번호를 입력하지 않았습니다.");
        }
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "이름을 입력하지 않았습니다.");
        }
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
