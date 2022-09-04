package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostCreateRequest {

    @NotEmpty
    private String board;

    @NotEmpty
    private String title;

    private String body;

    private Integer thumbnailIndex;

    public PostCreateRequest(String board, String title, String body) {
        this.board = board;
        this.title = title;
        this.body = body;
        this.thumbnailIndex = null;
    }
}
