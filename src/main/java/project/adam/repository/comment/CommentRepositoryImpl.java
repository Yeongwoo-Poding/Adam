package project.adam.repository.comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.post.Post;

import java.util.List;

import static project.adam.entity.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<Comment> findByPost(Post post, Pageable pageable) {
        List<Comment> contents = queryFactory.selectFrom(comment)
                .where(
                        comment.post.eq(post),
                        statusCondition()
                )
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    public BooleanExpression statusCondition() {
        return comment.status.eq(ContentStatus.PUBLISHED).or(comment.replies.isNotEmpty());
    }
}
