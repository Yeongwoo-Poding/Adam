package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;
import project.adam.utils.DateUtils;

@Getter
public class ReplyFindResponse {

    private Long id;
    private String writerName;
    private String createdDate;
    private boolean modified;
    private String body;

    public ReplyFindResponse(Reply reply) {
        this.id = reply.getId();
        this.writerName = reply.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(reply.getCreatedDate());
        this.modified = reply.isModified();
        this.body = reply.getBody();
    }
}
