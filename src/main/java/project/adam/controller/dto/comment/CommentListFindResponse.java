package project.adam.controller.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CommentListFindResponse {
    List<CommentFindResponse> comments = new ArrayList<>();
}
