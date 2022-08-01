package project.adam.service.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentCreateRequest {

    @NotEmpty
    private String writerId;

    @NotEmpty
    private String body;
}
