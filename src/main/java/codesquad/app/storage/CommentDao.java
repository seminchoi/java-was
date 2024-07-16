package codesquad.app.storage;

import codesquad.app.model.Comment;

import java.util.List;

public interface CommentDao extends CommonDao<Comment, Long> {
    List<Comment> findByPostId(Long postId);
}
