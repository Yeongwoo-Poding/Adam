package project.adam.repository.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.controller.dto.request.post.PostListFindCondition;
import project.adam.entity.post.Post;

import java.util.Optional;

public interface PostRepositoryCustom {

    Slice<Post> findPosts(PostListFindCondition condition, Pageable pageable);

    Optional<Post> showPost(Long postId);
}
