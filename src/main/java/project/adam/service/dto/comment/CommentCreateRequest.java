package project.adam.service.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    private Long parentId;

    @NotEmpty
    private String body;

    public CommentCreateRequest(String body) {
        this.parentId = null;
        this.body = body;
    }
}
