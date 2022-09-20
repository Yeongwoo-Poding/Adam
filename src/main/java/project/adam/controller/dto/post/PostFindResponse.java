package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.post.Post;
import project.adam.entity.post.PostImage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostFindResponse {
    private Long id;
    private String writerName;
    private String createdDate;
    private boolean modified;
    private String title;
    private String body;
    private int viewCount;
    private int commentCount;
    private List<String> images = new ArrayList<>();

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.writerName = post.getWriter().getName();
        this.createdDate = post.getFormattedCreatedDate();
        this.modified = post.isModified();
        this.viewCount = post.getViewCount();
        this.body = post.getBody();
        this.commentCount = post.getComments().size();
        this.images = post.getImages().stream()
                .map(PostImage::getName)
                .collect(Collectors.toList());
    }
}
