package codesquad.app.storage;

import codesquad.app.model.Post;
import codesquad.db.JdbcTemplateManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

public class RdbmsPostDao implements PostDao {
    private static final RowMapper<Post> postRowMapper = (rs, rowNum) -> {
        return new Post(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getString("content"),
                rs.getString("author_id")
        );
    };

    private final JdbcTemplate jdbcTemplate;

    public RdbmsPostDao(JdbcTemplateManager jdbcTemplateManager) {
        jdbcTemplate = jdbcTemplateManager.getJdbcTemplate();
    }

    @Override
    public void save(Post post) {
        final String sql = "INSERT INTO posts (title, content, author_id) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, post.getTitle(), post.getContent(), post.getAuthorId());
    }

    @Override
    public Optional<Post> findById(Long id) {
        final String sql = "SELECT * FROM posts WHERE id = ?";
        Post post = jdbcTemplate.queryForObject(sql, postRowMapper, id);
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findAll() {
        final String sql = "SELECT * FROM posts";
        return jdbcTemplate.query(sql, postRowMapper);
    }
}
