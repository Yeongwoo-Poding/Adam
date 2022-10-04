package project.adam.service.dto.reply;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.reply.ReplyUpdateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyUpdateServiceRequest {

    private Long replyId;
    private String body;

    public ReplyUpdateServiceRequest(Long replyId, ReplyUpdateControllerRequest request) {
        this.replyId = replyId;
        this.body = request.getBody();
    }
}
