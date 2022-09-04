package project.adam.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.entity.Comment;
import project.adam.entity.Post;

import java.util.List;

import static project.adam.entity.QComment.comment;
import static project.adam.entity.QCommentReport.commentReport;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Value("${report.hiddenCount}")
    private int reportHiddenCount;

    @Override
    public Slice<Comment> findRootCommentByPost(Post post, Pageable pageable) {
        List<Comment> contents = queryFactory.selectFrom(comment)
                .leftJoin(comment.replies)
                .fetchJoin()
                .where(
                        commentReport.count().lt(reportHiddenCount),
                        comment.post.eq(post),
                        comment.parent.isNull()
                )
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }
}
