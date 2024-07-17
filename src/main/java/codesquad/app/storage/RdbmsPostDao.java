package codesquad.app.storage;

import codesquad.app.model.Post;
import codesquad.container.Component;
import codesquad.db.ConnectionPoolManager;
import codesquad.db.QueryTemplate;
import codesquad.db.ResultSetMapper;

import java.util.List;
import java.util.Optional;

@Component
public class RdbmsPostDao implements PostDao {
    private static final ResultSetMapper<Post> postRowMapper = (resultSet) -> new Post(
            resultSet.getLong("id"),
            resultSet.getString("title"),
            resultSet.getString("content"),
            resultSet.getString("image_url"),
            resultSet.getString("author_id")
    );


    private final QueryTemplate queryTemplate;

    public RdbmsPostDao(ConnectionPoolManager connectionPoolManager) {
        queryTemplate = new QueryTemplate(connectionPoolManager.getDataSource());
    }

    @Override
    public void save(Post post) {
        final String sql = "INSERT INTO posts (title, content, image_url, author_id) VALUES (?, ?, ?, ?)";
        queryTemplate.update(sql, post.getTitle(), post.getContent(), post.getImageUrl(), post.getAuthorId());
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

    @Override
    public Long findPrevious(Long postId) {
        String sql = "SELECT id FROM posts WHERE id < ? LIMIT 1";
        Object previous =  queryTemplate.queryForObject(sql, postId);
        if (previous == null) {
            return null;
        }
        return (Long) previous;
    }

    @Override
    public Long findNext(Long postId) {
        String sql = "SELECT id FROM posts WHERE id > ? LIMIT 1";
        Object next =  queryTemplate.queryForObject(sql, postId);
        if (next == null) {
            return null;
        }
        return (Long) next;
    }
}
