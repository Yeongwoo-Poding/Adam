package project.adam.controller.dto.response.comment;

import lombok.Getter;
import project.adam.entity.comment.Comment;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentListFindResponse {

    private final List<CommentListContent> contents;

    public CommentListFindResponse(List<Comment> commentSlice) {
        this.contents = commentSlice.stream()
                .map(CommentListContent::new)
                .collect(Collectors.toList());
    }
}
