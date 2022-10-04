package project.adam.service.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.controller.dto.request.post.PostCreateControllerRequest;
import project.adam.entity.post.Board;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateServiceRequest {

    private String email;
    private Board board;
    private String title;
    private String body;

    public PostCreateServiceRequest(String email, PostCreateControllerRequest request) {
        this.email = email;
        this.board = request.getBoard();
        this.title = request.getTitle();
        this.body = request.getBody();
    }
}
