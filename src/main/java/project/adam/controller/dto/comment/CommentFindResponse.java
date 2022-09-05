package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;

import java.time.ZonedDateTime;

@Getter
public class CommentFindResponse {

    private Long id;
    private String writerName;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;
    private String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createDate = comment.getCreateDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.body = comment.getBody();
    }
}
