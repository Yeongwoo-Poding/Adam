package project.adam.controller.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.post.Post;

import java.time.ZonedDateTime;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListContent {
    private Long id;
    private String writerName;
    private String board;
    private ZonedDateTime createdDate;
    private boolean isModified;
    private String title;
    private int viewCount;
    private int commentCount;
    private String thumbnail;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.board = post.getBoard().toString();
        this.createdDate = post.getCreatedDate();
        this.isModified = post.isModified();
        this.writerName = post.getWriter().getName();
        this.viewCount = post.getViewCount();
        this.thumbnail = post.getThumbnailName();
        this.commentCount = post.getComments().size();
    }
}
