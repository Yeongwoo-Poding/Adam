package project.adam.repository.member;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import project.adam.entity.common.ContentStatus;
import project.adam.entity.member.Member;
import project.adam.entity.member.MemberStatus;
import project.adam.entity.member.QMember;

import javax.persistence.EntityManager;
import java.time.LocalDate;

import static project.adam.entity.comment.QComment.comment;
import static project.adam.entity.member.QMember.member;
import static project.adam.entity.post.QPost.post;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final EntityManager em;

    @Override
    public void remove(Member member) {
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

        em.flush();
        em.clear();
    }

    @Override
    public Member ban(Member writer, int days) {
        LocalDate suspendedDate = writer.getSuspendedDate().plusDays(days);
        queryFactory.update(member)
                .set(member.status, MemberStatus.SUSPENDED)
                .set(member.suspendedDate, suspendedDate)
                .where(member.eq(writer))
                .execute();

        em.flush();
        em.clear();
        return queryFactory.selectFrom(member)
                .where(member.eq(writer))
                .fetchOne();
    }
}
