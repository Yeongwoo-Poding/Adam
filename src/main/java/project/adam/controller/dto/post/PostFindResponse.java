package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.Post;
import project.adam.entity.PostImage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostFindResponse {
    private Long id;
    private String title;
    private String writerName;
    private ZonedDateTime createdDate;
    private int viewCount;
    private String body;
    private int commentCount;
    private List<String> imageNames = new ArrayList<>();

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writerName = post.getWriter().getName();
        this.createdDate = post.getCreateDate();
        this.viewCount = post.getViewCount();
        this.body = post.getBody();
        this.commentCount = post.getComments().size();
        this.imageNames = post.getImages().stream()
                .map(PostImage::getName)
                .collect(Collectors.toList());
    }
}
