package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.comment.Comment;

@Getter
public class CommentFindResponse {

    private Long id;
    private String writerName;
    private String createdDate;
    private boolean modified;
    private String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = comment.getFormattedCreatedDate();
        this.modified = comment.isModified();
        this.body = comment.getBody();
    }
}
