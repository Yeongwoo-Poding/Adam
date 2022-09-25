package project.adam.service.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.post.Board;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostCreateRequest {

    private Board board;

    @NotEmpty
    private String title;

    private String body;

    public PostCreateRequest(Board board, String title, String body) {
        this.board = board;
        this.title = title;
        this.body = body;
    }
}
