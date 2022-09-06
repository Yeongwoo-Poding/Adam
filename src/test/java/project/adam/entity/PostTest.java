package project.adam.entity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.member.Member;
import project.adam.entity.post.Board;
import project.adam.entity.post.Post;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class PostTest {

    @Test
    void test_update() {
        //given
        Member member = new Member(UUID.randomUUID(), "email1", "member");
        Post post = new Post(member, Board.FREE, "title1", "body1");

        //when
        post.update("title2", "body2");

        //then
        assertThat(post.getTitle()).isEqualTo("title2");
        assertThat(post.getBody()).isEqualTo("body2");
    }
}