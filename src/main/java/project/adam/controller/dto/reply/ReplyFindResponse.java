package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.time.ZonedDateTime;

@Getter
public class ReplyFindResponse {

    private Long id;
    private String writerName;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;
    private String body;

    public ReplyFindResponse(Reply reply) {
        this.id = reply.getId();
        this.writerName = reply.getWriter().getName();
        this.createDate = reply.getCreateDate();
        this.lastModifiedDate = reply.getLastModifiedDate();
        this.body = reply.getBody();
    }
}
