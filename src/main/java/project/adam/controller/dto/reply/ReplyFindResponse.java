package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.time.ZonedDateTime;

@Getter
public class ReplyFindResponse {

    private Long id;
    private String writerName;
    private ZonedDateTime createdDate;
    private boolean isModified;
    private String body;

    public ReplyFindResponse(Reply reply) {
        this.id = reply.getId();
        this.writerName = reply.getWriter().getName();
        this.createdDate = reply.getCreatedDate();
        this.isModified = reply.isModified();
        this.body = reply.getBody();
    }
}
