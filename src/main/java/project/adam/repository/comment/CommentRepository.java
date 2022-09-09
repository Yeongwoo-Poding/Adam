package project.adam.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Member;
import project.adam.entity.post.Post;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    List<Comment> findAllByPost(Post post);

    List<Comment> findAllByWriter(Member writer);

    @Query("select count(cr) from CommentReport cr where cr.comment.id = :commentId")
    int countCommentReportById(Long commentId);
}
