package project.adam.repository.reply;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.comment.Comment;
import project.adam.entity.reply.Reply;

import java.util.Optional;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @NotNull
    @Query("select r from Reply r join fetch r.writer where r.id = :id")
    Optional<Reply> findById(@NotNull Long id);

    @Query("select count(rr) from ReplyReport rr where rr.reply = :reply and rr.isChecked = false")
    int countReplyReport(Reply reply);

    @Modifying
    @Query("update Reply r set r.status = project.adam.entity.common.ContentStatus.REMOVED where r.comment = :comment")
    void removeAllByComment(Comment comment);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Reply r set r.status = project.adam.entity.common.ContentStatus.HIDDEN where r = :reply")
    void hide(Reply reply);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Reply r set r.status = project.adam.entity.common.ContentStatus.REMOVED where r = :reply")
    void remove(Reply reply);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Reply r set r.status = project.adam.entity.common.ContentStatus.PUBLISHED where r = :reply")
    void release(Reply reply);
}
