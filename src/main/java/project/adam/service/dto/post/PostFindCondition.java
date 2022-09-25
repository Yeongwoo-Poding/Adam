package project.adam.service.dto.post;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import project.adam.entity.post.Board;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostFindCondition {

    private Board board;
    private String content;

    public PostFindCondition(Board board, String content) {
        this.board = board;
        this.content = content;
    }
}
