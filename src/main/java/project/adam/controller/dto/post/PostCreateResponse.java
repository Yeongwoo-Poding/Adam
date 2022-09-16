package project.adam.controller.dto.post;

import lombok.Getter;
import project.adam.entity.post.Post;

import java.time.ZonedDateTime;

@Getter
public class PostCreateResponse {

    private Long id;
    private ZonedDateTime createDate;

    public PostCreateResponse(Post post) {
        this.id = post.getId();
        this.createDate = post.getCreateDate();
    }
}
