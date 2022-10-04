package project.adam.controller.dto.response.common;

import lombok.Getter;
import project.adam.entity.post.Board;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public class BoardListFindResponse {

    private final List<BoardListContent> boards;

    public BoardListFindResponse() {
        this.boards = Stream.of(Board.values())
                .map(board -> new BoardListContent(board.toString(), board.name))
                .collect(Collectors.toList());
    }
}
