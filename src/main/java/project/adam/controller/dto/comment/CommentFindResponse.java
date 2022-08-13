package project.adam.controller.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class CommentFindResponse {

    private Long id;

    private UUID writerId;

    private Long postId;

    private List<CommentFindResponse> children = new ArrayList<>();

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerId = comment.getWriter().getId();
        this.postId = comment.getPost().getId();
        this.children = comment.getChildren().stream().map(CommentFindResponse::new).collect(Collectors.toList());
        this.createDate = comment.getCreateDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.body = comment.getBody();
    }
}
