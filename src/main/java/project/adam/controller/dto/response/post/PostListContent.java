package project.adam.controller.dto.response.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.post.Post;
import project.adam.utils.DateUtils;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListContent {

    private final Long id;
    private final String writerName;
    private final String board;
    private final String createdDate;
    private final boolean modified;
    private final String title;
    private final int viewCount;
    private final int commentCount;
    private final String thumbnail;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.board = post.getBoard().toString();
        this.createdDate = DateUtils.getFormattedDateTime(post.getCreatedDate());
        this.modified = post.isModified();
        this.writerName = post.getWriter().getName();
        this.viewCount = post.getViewCount();
        this.thumbnail = post.getThumbnail() == null ? null : post.getThumbnail().getName();
        this.commentCount = post.getComments().size();
    }
}
