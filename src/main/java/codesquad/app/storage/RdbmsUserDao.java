package codesquad.app.storage;

import codesquad.app.model.User;
import codesquad.db.JdbcTemplateManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class RdbmsUserDao implements UserDao {
    private static final RowMapper<User> USER_ROW_MAPPER = (rs, rowNum) -> new User(
            rs.getString("user_id"),
            rs.getString("name"),
            rs.getString("password")
    );

    private final JdbcTemplate jdbcTemplate;

    public RdbmsUserDao(JdbcTemplateManager jdbcTemplateManager) {
        jdbcTemplate = jdbcTemplateManager.getJdbcTemplate();
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users (user_id, name, password) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, user.getUserId(), user.getName(), user.getPassword());

    }

    @Override
    public Optional<User> findById(String userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = jdbcTemplate.queryForObject(sql, USER_ROW_MAPPER, userId);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> findAll() {
        return Collections.emptyList();
    }
}
