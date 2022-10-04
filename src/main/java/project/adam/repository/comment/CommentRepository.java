package project.adam.repository.comment;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.comment.Comment;
import project.adam.entity.post.Post;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {

    @NotNull
    @Query("select c from Comment c join fetch c.writer where c.id = :id")
    Optional<Comment> findById(@NotNull Long id);

    @Query("select count(cr) from CommentReport cr where cr.comment = :comment and cr.isChecked = false")
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

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Comment c set c.status = project.adam.entity.common.ContentStatus.PUBLISHED where c = :comment")
    void release(Comment comment);
}
