package project.adam.service.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.post.Board;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    private Board board;

    @NotEmpty
    private String title;

    private String body;

    public PostCreateRequest(String boardId, String title, String body) {
        this.board = Board.valueOf(boardId);
        this.title = title;
        this.body = body;
    }
}
