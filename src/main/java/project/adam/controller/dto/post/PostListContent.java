package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.Post;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class PostListContent {
    private Long id;
    private UUID writerId;
    private String board;
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private String title;
    private String body;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.writerId = post.getWriter().getId();
        this.board = post.getBoard().toString();
        this.createDate= post.getCreateDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.title = post.getTitle();
        this.body = post.getBody();
    }
}
