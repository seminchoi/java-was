package codesquad.app.model;

import codesquad.exception.HttpException;
import codesquad.http.HttpStatus;

public class Post {
    private Long id;
    private String title;
    private String content;
    private String authorId;

    public Post(Long id, String title, String content, String authorId) {
        validateTitle(title);
        validateContent(content);

        this.id = id;
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    public Post(String title, String content, String authorId) {
        this.title = title;
        this.content = content;
        this.authorId = authorId;
    }

    private void validateTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "제목이 비어있습니다.");
        }
    }

    private void validateContent(String content) {
        if (content == null || content.isEmpty()) {
            throw new HttpException(HttpStatus.BAD_REQUEST, "내용이 비어있습니다.");
        }
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
