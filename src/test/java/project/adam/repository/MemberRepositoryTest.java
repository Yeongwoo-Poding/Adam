package project.adam.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Board;
import project.adam.entity.Member;
import project.adam.entity.Post;

import javax.persistence.EntityManager;
import java.util.List;
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
        Member member = new Member("id", "member1");
        memberRepository.save(member);

        //when
        Member findMember = memberRepository.findById(member.getId()).get();

        //then
        assertThat(findMember.getId()).isEqualTo(member.getId());
    }

    @Test
    void member_find_all() {
        //given
        Member member1 = new Member("id1", "member1");
        Member member2 = new Member("id2", "member2");
        memberRepository.save(member1);
        memberRepository.save(member2);

        //when
        List<Member> findMembers = memberRepository.findAll();

        //then
        List<String> findMemberIds = findMembers.stream().map(Member::getId).collect(Collectors.toList());
        assertThat(findMemberIds).contains("id1", "id2");
    }

    @Test
    void member_delete() {
        //given
        Member member = new Member("id", "member1");
        memberRepository.save(member);

        //when
        memberRepository.delete(member);

        //then
        assertThat(memberRepository.findById(member.getId()).isPresent()).isFalse();
    }

    @Test
    void find_all_post_by_member() {
        //given
        Member writer1 = memberRepository.save(new Member("id1", "writer1"));
        Member writer2 = memberRepository.save(new Member("id2", "writer2"));

        for (int i = 0; i < 100; i++) {
            Post newPost = new Post((i % 2 == 0) ? writer1 : writer2, Board.FREE, "post " + i, i + "th post body");
            postRepository.save(newPost);
        }

        //when
        List<Post> writer1Posts = postRepository.findAllByWriter(writer1);
        List<Post> writer2Posts = postRepository.findAllByWriter(writer2);

        //then
        assertThat(writer1Posts.size()).isEqualTo(50);
        assertThat(writer2Posts.size()).isEqualTo(50);
    }

    @Test
    void member_posts() {
        //given
        Member writer1 = memberRepository.save(new Member("id1", "writer1"));
        Member writer2 = memberRepository.save(new Member("id2", "writer2"));

        for (int i = 0; i < 100; i++) {
            Post newPost = new Post((i % 2 == 0) ? writer1 : writer2, Board.FREE, "post " + i, i + "th post body");
            postRepository.save(newPost);
        }

        //when
        List<Post> writer1Posts = postRepository.findAllByWriter(writer1);
        List<Post> writer2Posts = postRepository.findAllByWriter(writer2);

        //then
        assertThat(writer1.getPosts()).containsAll(writer1Posts);
        assertThat(writer2.getPosts()).containsAll(writer2Posts);
    }
}