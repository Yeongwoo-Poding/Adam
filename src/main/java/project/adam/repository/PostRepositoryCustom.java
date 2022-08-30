package project.adam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.entity.Post;
import project.adam.service.dto.post.PostFindCondition;

import java.util.Optional;

public interface PostRepositoryCustom {

    Slice<Post> findAll(PostFindCondition condition, Pageable pageable);

    Optional<Post> getPost(Long postId);
}
