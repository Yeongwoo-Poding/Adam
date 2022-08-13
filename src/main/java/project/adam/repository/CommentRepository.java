package project.adam.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.entity.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPost(Post post);

    List<Comment> findAllByWriter(Member writer);

    @Query("select c from Comment c left join fetch c.children where c.post = :post and c.parent is null")
    Slice<Comment> findRootCommentByPost(Post post, Pageable pageable);
}
