package project.adam.controller.dto.response.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReplyListFindResponse {
    private final List<ReplyListContent> contents;
    private final int size;

    public ReplyListFindResponse(List<Reply> replies) {
        this.contents = replies.stream()
                .map(ReplyListContent::new)
                .collect(Collectors.toList());
        this.size = replies.size();
    }
}
