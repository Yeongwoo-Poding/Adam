package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;

import java.time.ZonedDateTime;

@Getter
public class CommentUpdateResponse {
    private Long id;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;
    private String body;

    public CommentUpdateResponse(Comment comment) {
        this.id = comment.getId();
        this.createDate = comment.getCreateDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.body = comment.getBody();
    }
}
