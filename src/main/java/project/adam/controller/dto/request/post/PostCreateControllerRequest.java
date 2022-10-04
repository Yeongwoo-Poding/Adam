package project.adam.controller.dto.request.post;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import project.adam.entity.post.Board;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostCreateControllerRequest {

    @NotNull
    private Board board;

    @NotEmpty
    private String title;

    private String body;
}
