package project.adam.service.dto.comment;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.comment.CommentCreateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentCreateServiceRequest {

    private String email;
    private Long postId;
    private String body;

    public CommentCreateServiceRequest(String email, CommentCreateControllerRequest request) {
        this.email = email;
        this.postId = request.getPostId();
        this.body = request.getBody();
    }
}
