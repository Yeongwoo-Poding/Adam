package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.comment.Comment;

import java.time.ZonedDateTime;

@Getter
public class CommentFindResponse {

    private Long id;
    private String writerName;
    private ZonedDateTime createdDate;
    private boolean isModified;
    private String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = comment.getCreatedDate();
        this.isModified = comment.isModified();
        this.body = comment.getBody();
    }
}
