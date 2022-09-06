package project.adam.controller.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.post.Post;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListContent {
    private Long id;
    private String writerName;
    private String title;
    private int viewCount;
    private int commentCount;
    private String thumbnailName;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writerName = post.getWriter().getName();
        this.viewCount = post.getViewCount();
        this.thumbnailName = post.getThumbnailName();
        this.commentCount = post.getComments().size();
    }
}
