package project.adam.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;
import project.adam.repository.member.MemberRepository;
import project.adam.repository.post.PostRepository;
import project.adam.service.dto.post.PostFindCondition;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static project.adam.entity.member.Privilege.ADMIN;
import static project.adam.entity.member.Privilege.USER;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;

    @Test
    void post_save() {
        //given
        Member member = new Member(UUID.randomUUID(), "email1", "nickname");
        memberRepository.save(member);
        Post post = new Post(member, Board.FREE, "title1", "this is body");
        Post savedPost = postRepository.save(post);

        //when
        Post findPost = postRepository.findById(savedPost.getId()).get();

        //then
        assertThat(findPost.getTitle()).isEqualTo(post.getTitle());
        assertThat(findPost.getBody()).isEqualTo(post.getBody());
        assertThat(findPost.getWriter()).isEqualTo(post.getWriter());
        assertThat(findPost).isEqualTo(post);
    }

    @Test
    void post_condition_textLike() {
        //given
        Member adminMember = memberRepository.save(new Member(UUID.randomUUID(), "adminMember", "email1", ADMIN));
        Member userMember = memberRepository.save(new Member(UUID.randomUUID(), "userMember", "email2", USER));

        Post post1 = postRepository.save(new Post(adminMember, Board.FREE, "Post 1 success", "body 1"));
        Post post2 = postRepository.save(new Post(userMember, Board.FREE, "Post 2 success", "body 2"));
        Post post3 = postRepository.save(new Post(adminMember, Board.FREE, "Post 3 fail", "body 3"));
        Post post4 = postRepository.save(new Post(userMember, Board.FREE, "Post 4 fail", "body 4"));

        PageRequest allPages = PageRequest.of(0, 10);

        //when
        Slice<Post> findSuccessPost = postRepository.findAll(new PostFindCondition("success"), allPages);
        Slice<Post> findFailPost = postRepository.findAll(new PostFindCondition("fail"), allPages);

        //then
        assertThat(findSuccessPost).containsExactly(post1, post2);
        assertThat(findFailPost).containsExactly(post3, post4);
    }

    @Test
    void post_update() {
        //given
        Member member = new Member(UUID.randomUUID(), "email1", "nickname");
        memberRepository.save(member);
        Post post = new Post(member, Board.FREE, "beforeTitle", "before body");
        postRepository.save(post);

        //when
        post.update("afterTitle", "after body");

        //then
        assertThat(post.getTitle()).isEqualTo("afterTitle");
        assertThat(post.getBody()).isEqualTo("after body");
    }

    @Test
    void post_delete() {
        //given
        Member member = new Member(UUID.randomUUID(), "email1", "nickname");
        memberRepository.save(member);
        Post post = new Post(member, Board.FREE, "title1", "this is body");
        Post savedPost = postRepository.save(post);

        //when
        postRepository.delete(post);

        //then
        assertThat(postRepository.findById(post.getId()).isPresent()).isFalse();
    }
}