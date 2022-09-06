package project.adam.repository.reply;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import project.adam.entity.reply.Reply;

public interface ReplyRepository extends JpaRepository<Reply, Long>, ReplyRepositoryCustom {

    @Query("select count(rr) from ReplyReport rr where rr.reply.id = :replyId")
    int countReplyReportById(Long replyId);
}
