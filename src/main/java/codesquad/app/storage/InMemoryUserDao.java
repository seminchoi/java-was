package codesquad.app.storage;

import codesquad.app.model.User;
import codesquad.container.Component;
import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryUserDao implements UserDao {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public void save(User user) {
        users.compute(user.getUserId(), (userId, curUser) -> {
            if (curUser != null) {
                throw new HttpException(HttpStatus.CONFLICT);
            }
            return user;
        });
    }

    public Optional<User> findById(String userId) {
        User user = users.get(userId);
        return Optional.ofNullable(user);
    }

    public List<User> findAll() {
        return users.values().stream().toList();
    }
}
