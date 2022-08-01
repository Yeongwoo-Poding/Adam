package project.adam.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Board;
import project.adam.entity.Member;
import project.adam.entity.Post;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;

    @Test
    void post_save() {
        //given
        Member member = new Member("uuid", "nickname");
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
    void post_update() {
        //given
        Member member = new Member("uuid", "nickname");
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
        Member member = new Member("uuid", "nickname");
        memberRepository.save(member);
        Post post = new Post(member, Board.FREE, "title1", "this is body");
        Post savedPost = postRepository.save(post);

        //when
        postRepository.delete(post);

        //then
        assertThat(postRepository.findById(post.getId()).isPresent()).isFalse();
    }
}