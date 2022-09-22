package project.adam.service.dto.post;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.adam.entity.post.Board;

@Getter @Setter
@NoArgsConstructor
public class PostFindCondition {

    private Board board;
    private String content;

    public PostFindCondition(String boardId, String content) {
        this.board = Board.valueOf(boardId);
        this.content = content;
    }
}
