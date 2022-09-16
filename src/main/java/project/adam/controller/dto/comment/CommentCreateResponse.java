package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.comment.Comment;

import java.time.ZonedDateTime;

@Getter
public class CommentCreateResponse {

    private Long id;
    private ZonedDateTime createDate;

    public CommentCreateResponse(Comment comment) {
        this.id = comment.getId();
        this.createDate = comment.getCreateDate();
    }
}
