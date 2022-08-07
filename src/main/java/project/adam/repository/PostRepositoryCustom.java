package project.adam.repository;

import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.Post;

import java.util.List;

public interface PostRepositoryCustom {

    List<Post> findAll(PostFindCondition condition);
}
