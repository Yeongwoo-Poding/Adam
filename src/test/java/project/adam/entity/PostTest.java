package project.adam.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class PostTest {

    @Test
    void test_update() {
        //given
        Member member = new Member("uuid", "member");
        Post post = new Post(member, Board.FREE, "title1", "body1");

        //when
        post.update("title2", "body2");

        //then
        assertThat(post.getTitle()).isEqualTo("title2");
        assertThat(post.getBody()).isEqualTo("body2");
    }
}