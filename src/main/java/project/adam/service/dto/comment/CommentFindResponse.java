package project.adam.service.dto.comment;

import lombok.Getter;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.entity.Post;

import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Getter
public class CommentFindResponse {

    private Long id;

    private Long writerId;

    private Long postId;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String body;

    public CommentFindResponse(Comment comment) {
        this.id = comment.getId();
        this.writerId = comment.getWriter().getId();
        this.postId = comment.getPost().getId();
        this.body = comment.getBody();
    }
}
