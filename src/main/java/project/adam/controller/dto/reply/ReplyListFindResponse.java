package project.adam.controller.dto.reply;

import lombok.Getter;
import org.springframework.data.domain.Slice;
import project.adam.controller.dto.Paging;
import project.adam.entity.reply.Reply;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ReplyListFindResponse {
    private List<ReplyListContent> contents = new ArrayList<>();
    private Paging paging;
    private int size;
    private boolean hasNext;

    public ReplyListFindResponse(Slice<Reply> replySlice) {
        this.contents = replySlice.getContent().stream()
                .map(ReplyListContent::new)
                .collect(Collectors.toList());
        this.paging = new Paging(replySlice.getPageable());
        this.size = replySlice.getSize();
        this.hasNext = replySlice.hasNext();
    }
}
