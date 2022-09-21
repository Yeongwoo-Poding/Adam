package project.adam.repository.post;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.entity.post.Post;
import project.adam.service.dto.post.PostFindCondition;

import java.util.Optional;

public interface PostRepositoryCustom {

    Slice<Post> findPosts(PostFindCondition condition, Pageable pageable);

    Optional<Post> showPost(Long postId);
}
