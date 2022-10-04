package project.adam.service.dto.reply;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.reply.ReplyCreateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyCreateServiceRequest {

    private String email;
    private Long commentId;
    private String body;

    public ReplyCreateServiceRequest(String email, ReplyCreateControllerRequest request) {
        this.email = email;
        this.commentId = request.getCommentId();
        this.body = request.getBody();
    }
}
