package codesquad.app.storage;

import codesquad.app.model.Post;
import codesquad.db.ConnectionPoolManager;
import codesquad.db.QueryTemplate;
import codesquad.db.ResultSetMapper;

import java.util.List;
import java.util.Optional;

public class RdbmsPostDao implements PostDao {
    private static final ResultSetMapper<Post> postRowMapper = (resultSet) -> new Post(
            resultSet.getLong("id"),
            resultSet.getString("title"),
            resultSet.getString("content"),
            resultSet.getString("author_id")
    );


    private final QueryTemplate queryTemplate;

    public RdbmsPostDao(ConnectionPoolManager connectionPoolManager) {
        queryTemplate = new QueryTemplate(connectionPoolManager.getDataSource());
    }

    @Override
    public void save(Post post) {
        final String sql = "INSERT INTO posts (title, content, author_id) VALUES (?, ?, ?)";
        queryTemplate.update(sql, post.getTitle(), post.getContent(), post.getAuthorId());
    }

    @Override
    public Optional<Post> findById(Long id) {
        final String sql = "SELECT * FROM posts WHERE id = ?";
        Post post = queryTemplate.queryForObject(sql, postRowMapper, id);
        return Optional.ofNullable(post);
    }

    @Override
    public List<Post> findAll() {
        final String sql = "SELECT * FROM posts";
        return queryTemplate.query(sql, postRowMapper);
    }
}
