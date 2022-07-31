package project.adam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.adam.entity.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
