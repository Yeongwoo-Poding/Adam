package project.adam.repository.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.service.dto.post.PostFindCondition;

import java.util.List;
import java.util.Optional;

import static project.adam.entity.post.QPost.post;
import static project.adam.entity.post.QPostReport.postReport;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Value("${report.count}")
    private Long reportCount;

    @Override
    public Slice<Post> findPosts(PostFindCondition condition, Pageable pageable) {
        List<Post> contents = queryFactory.query()
                .select(post)
                .from(post)
                .leftJoin(post.thumbnail)
                .fetchJoin()
                .leftJoin(post.writer)
                .fetchJoin()
                .where(searchCondition(condition))
                .orderBy(post.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        boolean hasNext = (contents.size() > pageable.getPageSize());
        if (hasNext) {
            contents.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(contents, pageable, hasNext);
    }

    private BooleanBuilder searchCondition(PostFindCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(titleCondition(condition.getContent()));
        builder.or(bodyCondition(condition.getContent()));
        builder.and(boardCondition(condition.getBoard().toString()));
        builder.and(validateHiddenCondition());
        return builder;
    }

    private BooleanExpression titleCondition(String title) {
        return title == null ? null : post.title.like("%" + title + "%");
    }

    private BooleanExpression bodyCondition(String body) {
        return body == null ? null : post.body.like("%" + body + "%");
    }

    private BooleanExpression boardCondition(String boardId) {
        if (boardId == null) {
            return null;
        }
        return post.board.eq(Board.valueOf(boardId));
    }

    private BooleanExpression validateHiddenCondition() {
        return queryFactory.select(postReport.count())
                .from(postReport)
                .where(postReport.post.eq(post))
                .lt(reportCount);
    }

    @Override
    public Optional<Post> showPost(Long postId) {
        queryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        return Optional.ofNullable(
                queryFactory.selectFrom(post)
                        .leftJoin(post.writer)
                        .fetchJoin()
                        .leftJoin(post.comments)
                        .fetchJoin()
                        .where(post.id.eq(postId))
                        .fetchOne()
        );
    }
}
