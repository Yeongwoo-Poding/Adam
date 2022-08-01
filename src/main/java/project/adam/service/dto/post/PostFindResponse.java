package project.adam.service.dto.post;

import lombok.Getter;
import project.adam.entity.Post;
import java.time.LocalDateTime;

@Getter
public class PostFindResponse {

    private Long id;

    private Long writerId;

    private String boardName;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String title;

    private String body;

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.writerId = post.getWriter().getId();
        this.boardName = post.getBoard().toString();
        this.createDate= post.getCreateDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.title = post.getTitle();
        this.body = post.getBody();
    }
}
