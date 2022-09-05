package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.Board;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    private String board;

    @NotEmpty
    private String title;

    private String body;

    private Integer thumbnailIndex;

    public PostCreateRequest(String boardName, String title, String body) {
        this.board = boardName;
        this.title = title;
        this.body = body;
        this.thumbnailIndex = null;
    }
}
