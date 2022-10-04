package project.adam.controller.dto.response.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;
import project.adam.utils.DateUtils;

@Getter
public class ReplyFindResponse {

    private final Long id;
    private final String writerName;
    private final String createdDate;
    private final boolean modified;
    private final String body;

    public ReplyFindResponse(Reply reply) {
        this.id = reply.getId();
        this.writerName = reply.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(reply.getCreatedDate());
        this.modified = reply.isModified();
        this.body = reply.getBody();
    }
}
