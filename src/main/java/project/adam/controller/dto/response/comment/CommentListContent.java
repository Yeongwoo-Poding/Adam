package project.adam.controller.dto.response.comment;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.utils.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommentListContent {
    private final Long id;
    private final String writerName;
    private final String createdDate;
    private final boolean modified;
    private final String body;
    private final List<CommentListContent> children;

    public CommentListContent(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(comment.getCreatedDate());
        this.modified = comment.isModified();
        this.body = getBody(comment);
        this.children = getChildren(comment);
    }

    private String getBody(Comment comment) {
        if (comment.getStatus() == ContentStatus.HIDDEN) {
            return "숨겨진 댓글입니다.";
        } else if (comment.getStatus() == ContentStatus.REMOVED) {
            return "삭제된 댓글입니다.";
        } else {
            return comment.getBody();
        }
    }

    @Nullable
    private List<CommentListContent> getChildren(Comment comment) {
        if (comment.isRoot()) {
            return comment.getChildren()
                    .stream().map(CommentListContent::new)
                    .collect(Collectors.toList());
        }
        return null;
    }
}
