package project.adam.repository.reply;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.entity.reply.Reply;

import java.util.List;

import static project.adam.entity.reply.QReply.reply;

@RequiredArgsConstructor
public class ReplyRepositoryImpl implements ReplyRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Override
    public Slice<Reply> findAllByCommentId(Long commentId, Pageable pageable) {
        List<Reply> contents = queryFactory.selectFrom(reply)
                .where(
                        reply.comment.id.eq(commentId),
                        reply.reports.size().lt(reportHiddenCount)
                )
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }
}
