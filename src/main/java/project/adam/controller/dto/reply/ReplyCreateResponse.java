package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.time.ZonedDateTime;

@Getter
public class ReplyCreateResponse {

    private Long id;
    private ZonedDateTime createDate;
    private String body;

    public ReplyCreateResponse(Reply reply) {
        this.id = reply.getId();
        this.createDate = reply.getCreateDate();
        this.body = reply.getBody();
    }
}
