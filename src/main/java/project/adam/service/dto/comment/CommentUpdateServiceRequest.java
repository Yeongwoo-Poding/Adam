package project.adam.service.dto.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.comment.CommentUpdateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentUpdateServiceRequest {

    private Long commentId;
    private String body;

    public CommentUpdateServiceRequest(Long commentId, CommentUpdateControllerRequest request) {
        this.commentId = commentId;
        this.body = request.getBody();
    }
}
