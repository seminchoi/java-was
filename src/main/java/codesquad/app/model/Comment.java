package codesquad.app.model;

public class Comment {
    private Long id;
    private String content;
    private Long postId;
    private String authorId;

    public Comment(String content, Long postId, String authorId) {
        this.content = content;
        this.postId = postId;
        this.authorId = authorId;
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
