package project.adam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.entity.Comment;
import project.adam.entity.Post;

public interface CommentRepositoryCustom {
    Slice<Comment> findRootCommentByPost(Post post, Pageable pageable);
}
