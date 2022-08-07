package project.adam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    Slice<Post> findAll(PostFindCondition condition, Pageable pageable);
}
