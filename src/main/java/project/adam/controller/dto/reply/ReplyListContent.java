package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.time.LocalDateTime;

@Getter
public class ReplyListContent {
    private Long id;
    private String writerName;
    private LocalDateTime createdDate;
    private boolean modified;
    private String body;

    public ReplyListContent(Reply reply) {
        this.id = reply.getId();
        this.writerName = reply.getWriter().getName();
        this.createdDate = reply.getCreatedDate();
        this.modified = reply.isModified();
        this.body = reply.getBody();
    }
}
