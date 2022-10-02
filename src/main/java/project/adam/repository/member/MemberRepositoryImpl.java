package project.adam.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
import project.adam.entity.member.QMember;

import javax.persistence.EntityManager;

import static project.adam.entity.comment.QComment.comment;
import static project.adam.entity.post.QPost.post;
import static project.adam.entity.reply.QReply.reply;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public void remove(Member member) {
        queryFactory.update(reply)
                .set(reply.status, ContentStatus.REMOVED)
                .where(reply.writer.eq(member))
                .execute();

        queryFactory.update(comment)
                .set(comment.status, ContentStatus.REMOVED)
                .where(comment.writer.eq(member))
                .execute();

        queryFactory.update(post)
                .set(post.status, ContentStatus.REMOVED)
                .where(post.writer.eq(member))
                .execute();

        queryFactory.update(QMember.member)
                .set(QMember.member.status, MemberStatus.WITHDRAWN)
                .where(QMember.member.eq(member))
                .execute();

        em.clear();
    }
}
