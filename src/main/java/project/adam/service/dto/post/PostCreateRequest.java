package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    private Long memberId;
    private String boardName;
    private String title;
    private String body;
}
