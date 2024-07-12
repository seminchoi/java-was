package codesquad.app.storage;

import codesquad.app.model.User;
import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserStorage {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void saveUser(User user) {
        users.compute(user.getUserId(), (userId, curUser) -> {
            if (curUser != null) {
                throw new HttpException(HttpStatus.CONFLICT);
            }
            return user;
        });
    }

    public Optional<User> findByUserId(String userId) {
        User user = users.get(userId);
        return Optional.ofNullable(user);
    }

    public List<User> findAll() {
        return users.values().stream().toList();
    }
}
