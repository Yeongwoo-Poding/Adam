package project.adam.service.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.post.PostUpdateControllerRequest;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostUpdateServiceRequest {

    private Long postId;
    private String title;
    private String body;

    public PostUpdateServiceRequest(Long postId, PostUpdateControllerRequest request) {
        this.postId = postId;
        this.title = request.getTitle();
        this.body = request.getBody();
    }
}
