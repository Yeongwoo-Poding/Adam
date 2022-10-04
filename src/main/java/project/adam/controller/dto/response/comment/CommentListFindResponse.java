package project.adam.controller.dto.response.comment;

import lombok.Getter;
import org.springframework.data.domain.Slice;
import project.adam.controller.dto.Paging;
import project.adam.entity.comment.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentListFindResponse {

    private final List<CommentListContent> contents;
    private final Paging paging;
    private final int size;
    private final boolean hasNext;

    public CommentListFindResponse(Slice<Comment> commentSlice) {
        this.contents = commentSlice.getContent().stream()
                .map(CommentListContent::new)
                .collect(Collectors.toList());
        this.paging = new Paging(commentSlice.getPageable());
        this.size = commentSlice.getNumberOfElements();
        this.hasNext = commentSlice.hasNext();
    }
}
