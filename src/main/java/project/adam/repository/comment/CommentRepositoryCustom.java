package project.adam.repository.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.entity.comment.Comment;
import project.adam.entity.post.Post;

public interface CommentRepositoryCustom {

    Slice<Comment> findByPost(Post post, Pageable pageable);
}
