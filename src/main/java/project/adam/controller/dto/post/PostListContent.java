package project.adam.controller.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.Post;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListContent {
    private Long id;
    private UUID writerId;
    private String board;
    private ZonedDateTime createDate;
    private ZonedDateTime lastModifiedDate;
    private String title;
    private String body;
    private int views;
    private int commentCount;
    private String thumbnailName;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.writerId = post.getWriter().getId();
        this.board = post.getBoard().toString();
        this.createDate= post.getCreateDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.views = post.getViews();
        this.commentCount = post.getComments().size();
        this.thumbnailName = post.getThumbnailName();
    }
}
