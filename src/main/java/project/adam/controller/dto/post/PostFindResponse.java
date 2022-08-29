package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.Post;
import project.adam.entity.PostImage;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class PostFindResponse {

    private Long id;
    private UUID writerId;
    private String board;
    private LocalDateTime createDate;
    private LocalDateTime lastModifiedDate;
    private String title;
    private String body;
    private int views;
    private int commentCount;
    private List<String> imagePaths = new ArrayList<>();

    public PostFindResponse(Post post) {
        this.id = post.getId();
        this.writerId = post.getWriter().getId();
        this.board = post.getBoard().toString();
        this.createDate= post.getCreateDate();
        this.lastModifiedDate = post.getLastModifiedDate();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.views = post.getViews();
        this.commentCount = post.getComments().size();
        this.imagePaths = post.getImages().stream()
                            .map(PostImage::getName)
                            .collect(Collectors.toList());
    }
}
