package project.adam.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import project.adam.service.dto.post.PostFindCondition;
import project.adam.entity.Board;
import project.adam.entity.Member;
import project.adam.entity.Post;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MemberRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;

    @Test
    void member_save() {
        //given
        Member member = new Member(UUID.randomUUID(), "member1");
        memberRepository.save(member);

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void member_find_all() {
        //given
        Member member1 = new Member(UUID.randomUUID(), "member1");
        Member member2 = new Member(UUID.randomUUID(), "member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> findMembers = memberRepository.findAll();

        //then
        List<UUID> findMemberIds = findMembers.stream().map(Member::getId).collect(Collectors.toList());
        assertThat(findMemberIds).contains(member1.getId(), member2.getId());
    }

    @Test
    void member_delete() {
        //given
        Member member = new Member(UUID.randomUUID(), "member1");
        memberRepository.save(member);

        //when
        memberRepository.delete(member);

        //then
        assertThat(memberRepository.findById(member.getId()).isPresent()).isFalse();
    }

    @Test
    void find_all_post_by_member() {
        //given
        Member writer1 = memberRepository.save(new Member(UUID.randomUUID(), "writer1"));
        Member writer2 = memberRepository.save(new Member(UUID.randomUUID(), "writer2"));

        for (int i = 0; i < 100; i++) {
            Post newPost = new Post((i % 2 == 0) ? writer1 : writer2, Board.FREE, "post " + i, i + "th post body");
            postRepository.save(newPost);
        }

        PageRequest allPages = PageRequest.of(0, 100);

        //when
        Slice<Post> writer1Posts = postRepository.findAll(new PostFindCondition(null, writer1.getId(), null), allPages);
        Slice<Post> writer2Posts = postRepository.findAll(new PostFindCondition(null, writer2.getId(), null), allPages);

        //then
        assertThat(writer1Posts.getContent().size()).isEqualTo(50);
        assertThat(writer2Posts.getContent().size()).isEqualTo(50);
    }

    @Test
    void member_posts() {
        //given
        Member writer1 = memberRepository.save(new Member(UUID.randomUUID(), "writer1"));
        Member writer2 = memberRepository.save(new Member(UUID.randomUUID(), "writer2"));

        for (int i = 0; i < 100; i++) {
            Post newPost = new Post((i % 2 == 0) ? writer1 : writer2, Board.FREE, "post " + i, i + "th post body");
            postRepository.save(newPost);
        }

        PageRequest allPages = PageRequest.of(0, 100);

        //when
        Slice<Post> writer1Posts = postRepository.findAll(new PostFindCondition(null, writer1.getId(), null), allPages);
        Slice<Post> writer2Posts = postRepository.findAll(new PostFindCondition(null, writer2.getId(), null), allPages);

        //then
        assertThat(writer1.getPosts()).containsAll(writer1Posts);
        assertThat(writer2.getPosts()).containsAll(writer2Posts);
    }
}