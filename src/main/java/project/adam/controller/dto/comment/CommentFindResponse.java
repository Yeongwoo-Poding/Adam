package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;
import java.time.LocalDateTime;

@Getter
public class CommentFindResponse {

    private Long id;

    private String writerId;

    private Long postId;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerId = comment.getWriter().getId();
        this.postId = comment.getPost().getId();
        this.createDate = comment.getCreateDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.body = comment.getBody();
    }
}
