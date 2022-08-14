package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CommentCreateResponse {

    private Long commentId;
    private UUID writerId;
    private Long postId;
    private LocalDateTime createDate;
    private String body;

    public CommentCreateResponse(Comment comment) {
        this.commentId = comment.getId();
        this.writerId = comment.getWriter().getId();
        this.postId = comment.getPost().getId();
        this.createDate = comment.getCreateDate();
        this.body = comment.getBody();
    }
}
