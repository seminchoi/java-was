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
        String sql = "INSERT INTO comment (author_id, post_id, content) VALUES (?, ?, ?)";
        queryTemplate.update(sql, comment.getAuthorId(), comment.getPostId(), comment.getContent());
    }

    @Override
    public Optional<Comment> findById(Long id) {
        String sql = "SELECT * FROM comment WHERE id = ?";
        return Optional.ofNullable(queryTemplate.queryForObject(
                sql, resultSetMapper, id
        ));
    }

    @Override
    public List<Comment> findAll() {
        throw new UnsupportedOperationException("댓글은 전체 조회 기능을 지원하지 않습니다.");
    }
}
