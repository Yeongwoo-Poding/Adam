package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.time.ZonedDateTime;

@Getter
public class ReplyUpdateResponse {
    private Long id;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;
    private String body;

    public ReplyUpdateResponse(Reply reply) {
        this.id = reply.getId();
        this.createDate = reply.getCreateDate();
        this.lastModifiedDate = reply.getLastModifiedDate();
        this.body = reply.getBody();
    }
}
