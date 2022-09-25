package project.adam.repository.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.comment.Comment;
import project.adam.entity.member.Member;
import project.adam.entity.reply.Reply;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    @Query("select count(rr) from ReplyReport rr where rr.reply.id = :replyId")
    int countReplyReportById(Long replyId);

    List<Reply> findRepliesByWriter(Member member);

    List<Reply> findRepliesByComment(Comment comment);
}
