package project.adam.service.dto.post;

import lombok.Getter;
import project.adam.entity.Post;
import project.adam.service.dto.comment.CommentFindResponse;
import project.adam.service.dto.comment.CommentListFindResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostFindResponse {

    private Long id;

    private Long writerId;

    private String boardName;

    private LocalDateTime createDate;

    private LocalDateTime lastModifiedDate;

    private String title;

    private String body;

    private List<CommentFindResponse> comments;

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.writerId = post.getWriter().getId();
        this.boardName = post.getBoard().toString();
        this.createDate= post.getCreateDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.comments = post.getComments().stream()
                            .map(CommentFindResponse::new)
                            .collect(Collectors.toList());
    }
}
