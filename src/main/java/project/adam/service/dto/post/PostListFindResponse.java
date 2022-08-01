package project.adam.service.dto.post;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class PostListFindResponse {
    List<PostFindResponse> posts = new ArrayList<>();
}
