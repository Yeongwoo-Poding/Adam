package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.Post;

import java.time.LocalDateTime;

@Getter
public class PostCreateResponse {

    private Long postId;
    private String board;
    private LocalDateTime createDate;
    private String title;
    private String body;

    public PostCreateResponse(Post post) {
        this.postId = post.getId();
        this.board = post.getBoard().toString();
        this.createDate = post.getCreateDate();
        this.title = post.getTitle();
        this.body = post.getBody();
    }
}
