package project.adam.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.Post;
import project.adam.entity.Privilege;

import java.util.List;

import static project.adam.entity.QPost.post;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Post> findAll(PostFindCondition condition) {
        System.out.println("PostRepositoryImpl.findAll");
        return queryFactory.query()
                .select(post)
                .from(post)
                .where(
                        privilegeCondition(condition.getPrivilege()),
                        writerIdCondition(condition.getWriterId()),
                        titleCondition(condition.getTitleLike())
                )
                .fetch();
    }

    private BooleanExpression privilegeCondition(Privilege privilege) {
        return privilege == null ? null : post.writer.privilege.eq(privilege);
    }

    private BooleanExpression writerIdCondition(String id) {
        System.out.println("id = " + id);
        System.out.println("post.writer.id = " + post.writer.id);
        return id == null ? null : post.writer.id.eq(id);
    }

    private BooleanExpression titleCondition(String title) {
        return title == null ? null : post.title.like("%" + title + "%");
    }
}
