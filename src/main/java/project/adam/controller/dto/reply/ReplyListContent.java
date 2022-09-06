package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.time.ZonedDateTime;

@Getter
public class ReplyListContent {
    private Long id;
    private String writerName;
    private ZonedDateTime createdDate;
    private String body;

    public ReplyListContent(Reply reply) {
        this.id = reply.getId();
        this.writerName = reply.getWriter().getName();
        this.createdDate = reply.getCreateDate();
        this.body = reply.getBody();
    }
}
