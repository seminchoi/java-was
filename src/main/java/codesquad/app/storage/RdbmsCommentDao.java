package codesquad.app.storage;

import codesquad.app.model.Comment;
import codesquad.container.Component;
import codesquad.db.ConnectionPoolManager;
import codesquad.db.QueryTemplate;
import codesquad.db.ResultSetMapper;

import java.util.List;
import java.util.Optional;

@Component
public class RdbmsCommentDao implements CommentDao {
    private static final ResultSetMapper<Comment> resultSetMapper = (resultSet) -> new Comment(
            resultSet.getLong("id"),
            resultSet.getString("content"),
            resultSet.getLong("post_id"),
            resultSet.getString("author_id")
    );

    private final QueryTemplate queryTemplate;

    public RdbmsCommentDao(ConnectionPoolManager connectionPoolManager) {
        this.queryTemplate = new QueryTemplate(connectionPoolManager.getDataSource());
    }

    @Override
    public void save(Comment comment) {
        String sql = "INSERT INTO comments (author_id, post_id, content) VALUES (?, ?, ?)";
        queryTemplate.update(sql, comment.getAuthorId(), comment.getPostId(), comment.getContent());
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT id, content, post_id, author_id FROM comments WHERE id = ?";
        return Optional.ofNullable(queryTemplate.queryForObject(
                sql, resultSetMapper, id
        ));
    }

    @Override
    public List<Comment> findAll() {
        String sql = "SELECT id, content, post_id, author_id FROM comments";
        return queryTemplate.query(sql, resultSetMapper);
    }

    public List<Comment> findByPostId(Long postId) {
        String sql = "SELECT id, content, post_id, author_id FROM comments WHERE post_id = ?";
        return queryTemplate.query(sql, resultSetMapper, postId);
    }
}
