package codesquad.domain;

import codesquad.domain.user.User;
import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

import java.util.HashMap;
import java.util.Optional;

public class UserStorage {
    private final HashMap<String, User> users = new HashMap<>();

    public void saveUser(User user) {
        synchronized (users) {
            if(users.containsKey(user.getUserId())) {
                throw new HttpException(HttpStatus.CONFLICT);
            }
            users.put(user.getUserId(), user);
        }
    }

    public Optional<User> findByUserId(String userId) {
        User user = users.get(userId);
        return Optional.ofNullable(user);
    }
}
