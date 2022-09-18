package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.controller.dto.reply.ReplyListContent;
import project.adam.entity.comment.Comment;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentListContent {
    private Long id;
    private String writerName;
    private ZonedDateTime createdDate;
    private boolean modified;
    private String body;
    private List<ReplyListContent> replies = new ArrayList<>();

    public CommentListContent(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = comment.getCreatedDate();
        this.modified = comment.isModified();
        this.body = comment.getBody();
        this.replies = comment.getReplies().stream()
                .map(ReplyListContent::new)
                .collect(Collectors.toList());
    }
}
