package project.adam.repository.reply;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import project.adam.entity.reply.Reply;

public interface ReplyRepositoryCustom {

    Slice<Reply> findAllByCommentId(Long commentId, Pageable pageable);
}
