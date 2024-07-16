package codesquad.app.storage;

import codesquad.app.model.User;
import codesquad.db.ConnectionPoolManager;
import codesquad.db.QueryTemplate;
import codesquad.db.ResultSetMapper;
import org.h2.jdbcx.JdbcConnectionPool;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RdbmsUserDao implements UserDao {
    private static final ResultSetMapper<User> USER_ROW_MAPPER = (resultSet) -> new User(
            resultSet.getString("user_id"),
            resultSet.getString("name"),
            resultSet.getString("password")
    );

    private final QueryTemplate queryTemplate;

    public RdbmsUserDao(ConnectionPoolManager connectionPoolManager) {
        queryTemplate = new QueryTemplate(connectionPoolManager.getDataSource());
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (user_id, name, password) VALUES (?, ?, ?)";
        queryTemplate.update(sql, user.getUserId(), user.getName(), user.getPassword());

    }

    @Override
    public Optional<User> findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = queryTemplate.queryForObject(sql, USER_ROW_MAPPER, userId);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }
}
