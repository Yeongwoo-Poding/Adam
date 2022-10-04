package project.adam.controller.dto.response.comment;

import lombok.Getter;
import project.adam.entity.comment.Comment;
import project.adam.utils.DateUtils;

@Getter
public class CommentFindResponse {

    private final Long id;
    private final String writerName;
    private final String createdDate;
    private final boolean modified;
    private final String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(comment.getCreatedDate());
        this.modified = comment.isModified();
        this.body = comment.getBody();
    }
}
