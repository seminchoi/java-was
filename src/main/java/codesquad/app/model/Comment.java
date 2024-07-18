package codesquad.app.model;

import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

public class Comment {
    private Long id;
    private String content;
    private Long postId;
    private String authorId;

    public Comment(String content, Long postId, String authorId) {
        validateContent(content);

        this.content = content;
        this.postId = postId;
        this.authorId = authorId;
    }

    private void validateContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "내용이 비어있습니다.");
        }
    }

    public Comment(Long id, String content, Long postId, String authorId) {
        this.id = id;
        this.content = content;
        this.postId = postId;
        this.authorId = authorId;
    }

    public Long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public Long getPostId() {
        return postId;
    }

    public String getAuthorId() {
        return authorId;
    }
}
