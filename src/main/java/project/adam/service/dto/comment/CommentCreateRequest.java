package project.adam.service.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommentCreateRequest {
    private Long writerId;
    private Long postId;
    private String body;
}
