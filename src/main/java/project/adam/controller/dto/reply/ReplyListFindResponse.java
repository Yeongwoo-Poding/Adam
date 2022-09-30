package project.adam.controller.dto.reply;

import lombok.Getter;
import project.adam.entity.reply.Reply;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReplyListFindResponse {
    private List<ReplyListContent> contents = new ArrayList<>();
    private int size;

    public ReplyListFindResponse(List<Reply> replies) {
        this.contents = replies.stream()
                .map(ReplyListContent::new)
                .collect(Collectors.toList());
        this.size = replies.size();
    }
}
