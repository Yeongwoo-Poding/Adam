package project.adam.repository.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.comment.Comment;
import project.adam.entity.reply.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select count(rr) from ReplyReport rr where rr.reply = :reply")
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
}
