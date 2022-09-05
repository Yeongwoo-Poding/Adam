package project.adam.controller.dto.post;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import project.adam.entity.Post;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostListContent {
    private Long id;
    private String title;
    private String writerName;
    private int viewCount;
    private String thumbnailName;
    private int commentCount;

    public PostListContent(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writerName = post.getWriter().getName();
        this.viewCount = post.getViewCount();
        this.thumbnailName = post.getThumbnailName();
        this.commentCount = post.getComments().size();
    }
}
