package project.adam.repository.post;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import project.adam.controller.dto.request.post.PostListFindCondition;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static project.adam.entity.post.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public Slice<Post> findPosts(PostListFindCondition condition, Pageable pageable) {
        List<Post> contents = queryFactory.query()
                .select(post)
                .from(post)
                .leftJoin(post.thumbnail)
                .fetchJoin()
                .leftJoin(post.writer)
                .fetchJoin()
                .where(
                        searchCondition(condition),
                        post.status.eq(ContentStatus.PUBLISHED)
                )
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

    private BooleanBuilder searchCondition(PostListFindCondition condition) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.or(titleCondition(condition.getContent()));
        builder.or(bodyCondition(condition.getContent()));
        builder.and(boardCondition(condition.getBoard()));
        return builder;
    }

    private BooleanExpression titleCondition(String title) {
        return title == null ? null : post.title.like("%" + title + "%");
    }

    private BooleanExpression bodyCondition(String body) {
        return body == null ? null : post.body.like("%" + body + "%");
    }

    private BooleanExpression boardCondition(Board board) {
        if (board == null) {
            return null;
        }
        return post.board.eq(board);
    }

    @Override
    public Optional<Post> showPost(Long postId) {
        queryFactory.update(post)
                .set(post.viewCount, post.viewCount.add(1))
                .where(post.id.eq(postId))
                .execute();

        em.flush();
        em.clear();

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
