package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.post.Post;
import project.adam.entity.post.PostImage;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostCreateResponse {

    private Long id;
    private String board;
    private ZonedDateTime createDate;
    private String title;
    private String body;
    private List<String> imageNames = new ArrayList<>();

    public PostCreateResponse(Post post) {
        this.id = post.getId();
        this.board = post.getBoard().toString();
        this.createDate = post.getCreateDate();
        this.title = post.getTitle();
        this.body = post.getBody();
        this.imageNames = post.getImages().stream()
                .map(PostImage::getName)
                .collect(Collectors.toList());
    }
}
