package project.adam.controller.dto.request.post;

import lombok.*;
import project.adam.entity.post.Board;

@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class PostListFindCondition {

    private Board board;

    private String content;
}
