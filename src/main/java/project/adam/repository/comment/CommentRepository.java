package project.adam.repository.comment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.comment.Comment;
import project.adam.entity.post.Post;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @Query("select count(cr) from CommentReport cr where cr.comment = :comment")
    int countCommentReport(Comment comment);

    @Modifying
    @Query("update Comment c set c.status = project.adam.entity.common.ContentStatus.REMOVED where c.post = :post")
    void removeAllByPost(Post post);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Comment c set c.status = project.adam.entity.common.ContentStatus.HIDDEN where c = :comment")
    void hide(Comment comment);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Comment c set c.status = project.adam.entity.common.ContentStatus.REMOVED where c = :comment")
    void remove(Comment comment);
}
