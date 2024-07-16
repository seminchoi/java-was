package codesquad.app.model;

public class Post {
    private Long id;
    private String title;
    private String content;
    private String authorId;

    public Post(Long id, String title, String content, String authorId) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    public Post(String content, String authorId, String title) {
        this.content = content;
        this.authorId = authorId;
        this.title = title;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getAuthorId() {
        return authorId;
    }
}
