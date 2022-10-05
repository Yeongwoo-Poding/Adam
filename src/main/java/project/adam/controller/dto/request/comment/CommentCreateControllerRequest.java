package project.adam.controller.dto.request.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class CommentCreateControllerRequest {

    @NotNull
    public Long postId;

    public Long parentId;

    @NotEmpty
    private String body;
}
