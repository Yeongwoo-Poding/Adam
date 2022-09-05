package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentListContent {
    private Long id;
    private String writerName;
    private ZonedDateTime createdDate;
    private String body;
    private List<CommentListContent> replies;

    public CommentListContent(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = comment.getCreateDate();
        this.body = comment.getBody();
        this.replies = comment.getReplies().stream()
                .map(CommentListContent::new)
                .collect(Collectors.toList());
    }
}
