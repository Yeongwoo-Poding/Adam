package project.adam.controller.dto.response.post;

import lombok.Getter;
import org.springframework.data.domain.Slice;
import project.adam.controller.dto.Paging;
import project.adam.entity.post.Post;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class PostListFindResponse {

    private final List<PostListContent> contents;
    private final Paging paging;
    private final int size;
    private final boolean hasNext;

    public PostListFindResponse(Slice<Post> postSlice) {
        this.contents = postSlice.getContent().stream()
                .map(PostListContent::new)
                .collect(Collectors.toList());
        this.paging = new Paging(postSlice.getPageable());
        this.size = postSlice.getNumberOfElements();
        this.hasNext = postSlice.hasNext();
    }
}
