package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.post.Post;
import project.adam.entity.post.PostImage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostUpdateResponse {
    private Long id;
    private ZonedDateTime createdDate;
    private String title;
    private String body;
    private int viewCount;
    private int commentCount;
    private List<String> imageNames = new ArrayList<>();

    public PostUpdateResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.createdDate = post.getCreateDate();
        this.viewCount = post.getViewCount();
        this.body = post.getBody();
        this.commentCount = post.getComments().size();
        this.imageNames = post.getImages().stream()
                .map(PostImage::getName)
                .collect(Collectors.toList());
    }
}
