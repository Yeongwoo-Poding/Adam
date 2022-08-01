package project.adam.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentTest {

    @Test
    void test_update() {
        //given
        Member member = new Member("uuid", "member");
        Post post = new Post(member, Board.FREE, "title1", "body1");
        Comment comment1 = new Comment(member, post, "comment1");

        //when
        comment1.update("comment2");

        //then
        assertThat(comment1.getBody()).isEqualTo("comment2");
    }

}