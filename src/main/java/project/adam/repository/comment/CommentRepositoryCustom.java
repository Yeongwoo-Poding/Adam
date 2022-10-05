package project.adam.repository.comment;

import project.adam.entity.comment.Comment;
import project.adam.entity.post.Post;

import java.util.List;

public interface CommentRepositoryCustom {

    List<Comment> findRootCommentsByPost(Post post);
}
