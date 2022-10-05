package project.adam.repository.comment;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import project.adam.entity.comment.Comment;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.post.Post;

import java.util.List;

import static project.adam.entity.comment.QComment.comment;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findRootCommentsByPost(Post post) {
        return queryFactory.selectFrom(comment)
                .where(
                        comment.post.eq(post),
                        comment.parent.isNull(),
                        statusCondition()
                )
                .fetch();
    }

    public BooleanExpression statusCondition() {
        return comment.status.eq(ContentStatus.PUBLISHED).or(comment.children.isNotEmpty());
    }
}
