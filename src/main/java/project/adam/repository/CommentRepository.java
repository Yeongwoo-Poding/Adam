package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.entity.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    List<Comment> findAllByWriter(Member writer);
}
