package codesquad.app.storage;

import codesquad.app.model.Post;

public interface PostDao extends CommonDao<Post, Long> {
    Long findPrevious(Long postId);
    Long findNext(Long postId);
}
