package project.adam.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import project.adam.entity.Board;
import project.adam.entity.Comment;
import project.adam.entity.Member;
import project.adam.entity.Post;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CommentRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;

    @Test
    void comment_save() {
        //given
        Member postWriter = new Member(UUID.randomUUID(), "postWriter");
        memberRepository.save(postWriter);
        Member commentWriter = new Member(UUID.randomUUID(), "commentWriter");
        memberRepository.save(commentWriter);

        Post post = new Post(postWriter, Board.FREE, "title", "body");
        postRepository.save(post);

        Comment comment = new Comment(commentWriter, post, null, "comment");
        commentRepository.save(comment);

        //when
        Comment findComment = commentRepository.findById(comment.getId()).get();

        //then
        assertThat(findComment).isEqualTo(comment);
        assertThat(findComment.getWriter()).isEqualTo(commentWriter);
        assertThat(findComment.getPost().getWriter()).isEqualTo(postWriter);
    }

    @Test
    void comment_update() {
        //given
        Member postWriter = new Member(UUID.randomUUID(), "postWriter");
        memberRepository.save(postWriter);
        Member commentWriter = new Member(UUID.randomUUID(), "commentWriter");
        memberRepository.save(commentWriter);

        Post post = new Post(postWriter, Board.FREE, "title", "body");
        postRepository.save(post);

        Comment comment = new Comment(commentWriter, post, null, "comment");
        commentRepository.save(comment);

        //when
        comment.update("changedCommit");

        //then
        assertThat(comment.getBody()).isEqualTo("changedCommit");
    }

    @Test
    void comment_delete() {
        //given
        Member postWriter = new Member(UUID.randomUUID(), "postWriter");
        memberRepository.save(postWriter);
        Member commentWriter = new Member(UUID.randomUUID(), "commentWriter");
        memberRepository.save(commentWriter);

        Post post = new Post(postWriter, Board.FREE, "title", "body");
        postRepository.save(post);

        Comment comment = new Comment(commentWriter, post, null, "comment");
        commentRepository.save(comment);

        //when
        commentRepository.delete(comment);

        //then
        assertThat(commentRepository.findById(comment.getId()).isPresent()).isFalse();
    }
}