package project.adam.controller.dto.response.comment;

import lombok.Getter;
import project.adam.controller.dto.response.reply.ReplyListContent;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.utils.DateUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class CommentListContent {
    private final Long id;
    private final String writerName;
    private final String createdDate;
    private final boolean modified;
    private final String body;
    private final List<ReplyListContent> replies;

    public CommentListContent(Comment comment) {
        this.id = comment.getId();
        this.writerName = comment.getWriter().getName();
        this.createdDate = DateUtils.getFormattedDateTime(comment.getCreatedDate());
        this.modified = comment.isModified();
        this.body = getBody(comment);
        this.replies = comment.getReplies().stream()
                .map(ReplyListContent::new)
                .collect(Collectors.toList());
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
}
