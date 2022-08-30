package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CommentListContent {
    private Long commentId;
    private UUID writerId;
    private Long postId;
    private List<CommentListContent> replies = new ArrayList<>();
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;
    private String body;

    public CommentListContent(Comment comment) {
        this.commentId = comment.getId();
        this.writerId = comment.getWriter().getId();
        this.postId = comment.getPost().getId();
        this.replies = comment.getReplies().stream()
                .map(CommentListContent::new)
                .collect(Collectors.toList());
        this.createDate = comment.getCreateDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.body = comment.getBody();
    }
}
